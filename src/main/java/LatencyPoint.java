import java.util.Arrays;

/**
 * @author lzn
 * @date 2024/01/06 22:44
 * @description
 */
public class LatencyPoint {

    /**
     * Two challenges for using object array:
     * 1. Thread-safe guaranteed
     * 2. Proper initial capacity
     */
    private static LatencyMetrics[] VALUES = new LatencyMetrics[0];
    private static final Object VALUE_LOCK = new Object();
    private final int id;

    public LatencyPoint(String event) {
        synchronized (VALUE_LOCK) {
            final LatencyMetrics[] current = VALUES;
            final int newId = current.length;
            LatencyMetrics[] copy = Arrays.copyOf(VALUES, newId + 1);
            copy[newId] = new LatencyMetrics(event);
            VALUES = copy;
            id = newId;
        }
    }

    public void start() {
        VALUES[id].start();
    }

    public void stop() {
        VALUES[id].compute();
    }

    public LatencyMetrics getMetrics() {
        return VALUES[id];
    }

    public static LatencyMetrics[] getValues() {
        return VALUES;
    }

    public int getId() {
        return id;
    }

    public static LatencyPoint init(String event) {
        return new LatencyPoint(event);
    }
}
