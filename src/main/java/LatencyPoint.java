import net.openhft.chronicle.bytes.Bytes;

import java.time.Duration;
import java.time.Instant;

/**
 * @author lzn
 * @date 2024/01/06 22:34
 * @description Measure the latency for events
 */
public class LatencyPoint {

    private Instant start;
    private String blockName;
    private PointType type;
    private int count;
    private long total;
    private long max;
    private long min = Integer.MAX_VALUE;
    private long mean;
    private final Bytes<byte[]> metricBytes;

    public LatencyPoint(String blockName, PointType type){
        this.blockName = blockName;
        this.type = type;
        metricBytes = Bytes.allocateElasticOnHeap(40);
    }

    public void start(){
        /*
        The Instant class represents an instant on the timeline. Basically, it is a numeric timestamp since the standard Java epoch of 1970-01-01T00:00:00Z.
        This method allows passing in an optional Clock parameter. If omitted, it uses the system clock in the default time zone.
         */
        start = Instant.now();
    }

    public void stop(){
        LatencyPointAggregator.INSTANCE.collect(collectMetrics(Duration.between(start, Instant.now()).getNano()));
    }

    private byte[] collectMetrics(long latency){
        return metricBytes.clear().writeUtf8(Thread.currentThread().getName()).writeUtf8(blockName).writeInt(type.ordinal()).writeLong(latency).toByteArray();
    }

    public LatencyPoint compute(long elapsedTime){
        max = Math.max(max, elapsedTime);
        min = Math.max(min, elapsedTime);
        total += elapsedTime;
        mean = total / ++count;
        return this;
    }

    public void copyFor(LatencyPoint target){
        target.blockName = blockName;
        target.type = type;
        target.max = max;
        target.min = min;
        target.mean = mean;
        target.count = count;
    }

    public void clear(){
        blockName = null;
        type = null;
        max = 0L;
        min = 0L;
        mean = 0L;
        count = 0;
    }

    public void increment(){
        ++count;
    }

    public void decrement(){
        --count;
    }

    @Override
    public String toString() {
        return "LatencyPoint{" +
                "event=" + blockName +
                ", type=" + type +
                ", count=" + count +
                ", max=" + max +
                ", min=" + min +
                ", mean=" + mean +
                '}';
    }
}
