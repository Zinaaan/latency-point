import java.time.Duration;
import java.time.Instant;

/**
 * @author lzn
 * @date 2024/01/06 22:34
 * @description Measure the latency for events
 */
public class LatencyMetrics {

    private Instant start;
    private int count;
    private long total;
    private long max;
    private long min = Integer.MAX_VALUE;
    private long mean;

    public void start(){
        /*
        The Instant class represents an instant on the timeline. Basically, it is a numeric timestamp since the standard Java epoch of 1970-01-01T00:00:00Z.
        This method allows passing in an optional Clock parameter. If omitted, it uses the system clock in the default time zone.
         */
        start = Instant.now();
    }

    public void compute(){
        long elapsedTime = Duration.between(start, Instant.now()).getNano();
        max = Math.max(max, elapsedTime);
        min = Math.max(min, elapsedTime);
        total += elapsedTime;
        mean = total / ++count;
    }

    public void compute(long elapsedTime){
        max = Math.max(max, elapsedTime);
        min = Math.max(min, elapsedTime);
        total += elapsedTime;
        mean = total / ++count;
    }

    public void increment(){
        ++count;
    }

    public void decrement(){
        --count;
    }

    @Override
    public String toString() {
        return "LatencyMetrics{" +
                "count=" + count +
                ", max=" + max +
                ", min=" + min +
                ", mean=" + mean +
                '}';
    }
}
