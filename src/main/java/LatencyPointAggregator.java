import net.openhft.chronicle.core.Jvm;
import org.agrona.collections.Object2ObjectHashMap;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author lzn
 * @date 2024/01/14 15:10
 * @description Collect and aggregate the latency points data
 */
public class LatencyPointAggregator implements Runnable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyPointAggregator.class);
    private final OneToOneConcurrentArrayQueue<byte[]> QUEUE = new OneToOneConcurrentArrayQueue<>(Integer.parseInt(System.getProperty("latency.point.queue.size", "1024")));
    private volatile boolean isRunning = true;
    private boolean collectionInProgress = true;
    private final ThreadLocal<MetricBuffer> reusableBytes = ThreadLocal.withInitial(() -> new MetricBuffer(40));
    public static final LatencyPointAggregator INSTANCE = new LatencyPointAggregator();
    public final Object2ObjectHashMap<MetricBuffer, LatencyPoint> latencyMap = new Object2ObjectHashMap<>();
    private final PointType[] types = PointType.values();

    private LatencyPointAggregator() {

    }

    public void addClientInfo(String threadName, String blockName, PointType type) {
        latencyMap.put(generateClientInfo(threadName, blockName, type), new LatencyPoint(blockName, type));
    }

    private MetricBuffer generateClientInfo(String threadName, String blockName, PointType type) {
        MetricBuffer buf = new MetricBuffer(threadName.length() + blockName.length() + Integer.BYTES * 3);
        buf.writeUtf8(threadName).writeUtf8(blockName).writeInt(type.ordinal());
        return buf;
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
                LOG.error("Error on aggregating metrics: [{}]", e.toString());
            }
        }
    }

    private void aggregate(byte[] metrics) {
        MetricBuffer buf = reusableBytes.get();
        buf.clear().write(metrics);
        String threadName = buf.readUtf8();
        String blockName = buf.readUtf8();
        PointType type = types[buf.readInt()];
        long latency = buf.readLong();
//        Map<String, LatencyPoint> latencyPointMap = Optional.ofNullable(LatencyPointsDistributor.getGlobalLatencyPointMap().get(threadName)).orElseThrow(() -> new RuntimeException("No thread exists: " + threadName));
//        LatencyPoint point = Optional.ofNullable(latencyPointMap.get(blockName)).orElseThrow(() -> new RuntimeException("No block exists: " + blockName));
        latencyMap.get(buf).compute(latency);
    }

    public void startCollection() {
        collectionInProgress = true;
    }

    public void stopCollection() {
        collectionInProgress = false;
        latencyMap.values().forEach(LatencyPoint::clear);
    }

    public void collect(byte[] points) {
        boolean isOffered = QUEUE.offer(points);
        if (!isOffered) {
            LOG.warn("Queue is full, points data will be no longer collected");
        }
    }

    @Override
    public void close() throws IOException {
        isRunning = false;
    }
}
