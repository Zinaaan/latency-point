import java.time.Duration;
import java.time.Instant;

/**
 * @author lzn
 * @date 2024/01/06 22:34
 * @description Measure the latency for events
 */
public class LatencyPoint {

    private Instant start;
    private String event;
    private PointType type;
    private int count;
    private long total;
    private long max;
    private long min = Integer.MAX_VALUE;
    private long mean;

    public LatencyPoint(String event, PointType type){
        this.event = event;
        this.type = type;
    }

    public void start(){
        /*
        The Instant class represents an instant on the timeline. Basically, it is a numeric timestamp since the standard Java epoch of 1970-01-01T00:00:00Z.
        This method allows passing in an optional Clock parameter. If omitted, it uses the system clock in the default time zone.
         */
        start = Instant.now();
    }

    public void stop(){
        compute();
    }

    public void compute(){
        compute(Duration.between(start, Instant.now()).getNano());
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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getCount() {
        return count;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMean() {
        return mean;
    }

    public void setMean(long mean) {
        this.mean = mean;
    }

    @Override
    public String toString() {
        return "LatencyPoint{" +
                "event=" + event +
                "type=" + type +
                "count=" + count +
                ", max=" + max +
                ", min=" + min +
                ", mean=" + mean +
                '}';
    }
}
