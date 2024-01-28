package core;

import common.MetricBuffer;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.core.Jvm;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author lzn
 * @date 2024/01/14 15:10
 * @description Collect and report metrics
 */
@Slf4j
public class MetricManager implements Runnable, Closeable {

    public static final MetricManager INSTANCE = new MetricManager();
    private static final OneToOneConcurrentArrayQueue<byte[]> QUEUE = new OneToOneConcurrentArrayQueue<>(Integer.parseInt(System.getProperty("latency.point.queue.size", "10240")));
    private volatile boolean isRunning = true;
    private boolean collectionInProgress = true;
    private final MetricBuffer buf = new MetricBuffer(40);
    private final MetricsReporter reporter;

    private MetricManager() {
        reporter = MetricsReporterFactory.getReporter();
        Executors.newSingleThreadScheduledExecutor().schedule(this, 5, TimeUnit.SECONDS);
        log.info("Metric manager has been initialized");
    }

    public void addLatencyPoint(LatencyPoint point) {
        if (reporter instanceof RemoteMetricReporter) {
            ((RemoteMetricReporter) reporter).getAggMetrics().put(new MetricBuffer().createKey(point.threadName(), point.blockName(), point.type()), point);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            if (!collectionInProgress || QUEUE.isEmpty()) {
                Jvm.pause(1000);
                continue;
            }
            try {
                byte[] metrics;
                if ((metrics = QUEUE.poll()) != null) {
                    aggregate(metrics);
                }
            } catch (Exception e) {
                log.error("Error on aggregating metrics: [{}]", e.toString());
            }
        }
    }

    private void aggregate(byte[] metrics) {
        reporter.report(buf.wrapBytes(metrics));
    }

    public void startCollection() {
        collectionInProgress = true;
    }

    public void stopCollection() {
        collectionInProgress = false;
    }

    public static void collect(byte[] points) {
        boolean isOffered = QUEUE.offer(points);
        if (!isOffered) {
            log.warn("Queue is full, points data will be no longer collected");
        }
    }

    @Override
    public void close() {
        isRunning = false;
    }
}
