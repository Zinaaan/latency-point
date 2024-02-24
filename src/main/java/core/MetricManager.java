package core;

import common.TraceBuffer;
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
    private boolean reportInProgress = false;
    private final TraceBuffer buf = new TraceBuffer(40);
    private final MetricsReporter reporter;

    private MetricManager() {
        reporter = MetricsReporterFactory.getReporter();
        Executors.newSingleThreadScheduledExecutor().schedule(this, 5, TimeUnit.SECONDS);
        log.info("Metric manager has been initialized");
    }

    @Override
    public void run() {
        while (isRunning) {
            if (reportInProgress || QUEUE.isEmpty()) {
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
        buf.putByte(0, (byte) metrics.length);
        buf.putBytes(1, metrics);
        reporter.report(buf);
    }

    public void startReporting() {
        reportInProgress = true;
    }

    public void stopReporting() {
        reportInProgress = false;
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
