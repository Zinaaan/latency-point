package core;

import common.MetricBuffer;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.core.Jvm;
import org.agrona.collections.Object2ObjectHashMap;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author lzn
 * @date 2024/01/14 15:10
 * @description Collect and aggregate the latency points data
 */
@Slf4j
public class LatencyPointAggregator implements Runnable, Closeable {

    private static final OneToOneConcurrentArrayQueue<byte[]> QUEUE = new OneToOneConcurrentArrayQueue<>(Integer.parseInt(System.getProperty("latency.point.queue.size", "1024")));
    private volatile boolean isRunning = true;
    private boolean collectionInProgress = true;
    private final MetricBuffer buf = new MetricBuffer(40);
    public static final LatencyPointAggregator INSTANCE = new LatencyPointAggregator();
    public final Object2ObjectHashMap<MetricBuffer, LatencyPoint> latencyMap = new Object2ObjectHashMap<>();
    private MetricsReporter reporter;

    private LatencyPointAggregator() {

    }

    public void addLatencyPoint(LatencyPoint point) {
        latencyMap.put(new MetricBuffer().createKey(point.threadName(), point.blockName(), point.type()), point);
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
        buf.wrapBytes(metrics);
    }

    public void startCollection() {
        collectionInProgress = true;
    }

    public void stopCollection() {
        collectionInProgress = false;
        latencyMap.values().forEach(LatencyPoint::clear);
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
