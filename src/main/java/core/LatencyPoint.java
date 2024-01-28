package core;

import common.PointType;
import lombok.Data;
import lombok.experimental.Accessors;
import net.openhft.chronicle.bytes.Bytes;

import java.time.Duration;
import java.time.Instant;

import static core.MetricManager.collect;

/**
 * @author lzn
 * @date 2024/01/06 22:34
 * @description Records a metric within the processing of blockName according to point type
 */
@Data
@Accessors(fluent = true)
public class LatencyPoint {

    private Instant startTime;
    private String threadName;
    private String blockName;
    private PointType type;
    private int count;
    private long total;
    private long max;
    private long min = Integer.MAX_VALUE;
    private long mean;
    private final Bytes<byte[]> metricBytes;

    public LatencyPoint(String threadName, String blockName, PointType type) {
        this.threadName = threadName;
        this.blockName = blockName;
        this.type = type;
        metricBytes = Bytes.allocateElasticOnHeap(40);
    }

    public void start() {
        /*
        The Instant class represents an instant on the timeline. Basically, it is a numeric timestamp since the standard Java epoch of 1970-01-01T00:00:00Z.
        This method allows passing in an optional Clock parameter. If omitted, it uses the system clock in the default time zone.
         */
        startTime = Instant.now();
    }

    public void stop() {
        collect(createMetrics(Duration.between(startTime, Instant.now()).getNano()));
    }

    public LatencyPoint compute(long elapsedTime) {
        max = Math.max(max, elapsedTime);
        min = Math.max(min, elapsedTime);
        total += elapsedTime;
        mean = total / ++count;
        return this;
    }

    public void copyFor(LatencyPoint target) {
        target.threadName = threadName;
        target.blockName = blockName;
        target.type = type;
        target.max = max;
        target.min = min;
        target.mean = mean;
        target.count = count;
    }

    public void clear() {
        max = 0L;
        min = 0L;
        mean = 0L;
        count = 0;
        total = 0L;
    }

    public void increment() {
        ++count;
        collect(createMetrics(count));
    }

    public void decrement() {
        --count;
        collect(createMetrics(count));
    }

    private byte[] createMetrics(long metric) {
        return metricBytes.clear().writeUtf8(Thread.currentThread().getName()).writeUtf8(blockName).writeInt(type.ordinal()).writeLong(metric).toByteArray();
    }



    @Override
    public String toString() {
        return "core.LatencyPoint{" +
                "event=" + blockName +
                ", type=" + type +
                ", count=" + count +
                ", max=" + max +
                ", min=" + min +
                ", mean=" + mean +
                '}';
    }
}
