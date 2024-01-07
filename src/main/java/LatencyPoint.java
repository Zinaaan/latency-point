import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author lzn
 * @date 2024/01/06 22:44
 * @description
 */
public class LatencyPoint {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyPoint.class);
    /**
     * Two challenges for using primitive array:
     * 1. Thread-safe
     * 2. Proper initial capacity
     */
    private static LatencyMetrics[] VALUES = new LatencyMetrics[0];
    private static final Object VALUE_LOCK = new Object();
    private final int id;

    public LatencyPoint(String event) {
         synchronized (VALUE_LOCK){
             int newId = VALUES.length;
             VALUES = Arrays.copyOf(VALUES, newId + 1);
             VALUES[newId] = new LatencyMetrics(event);
             this.id = newId;
         }
    }

    public void start(){
        VALUES[id].start();
    }

    public void stop(){
        VALUES[id].compute();
    }

    public LatencyMetrics getMetrics(){
//        LOG.info("Thread: {}, id: {}, length: {}", Thread.currentThread().getName(), id, VALUES.length);
        return VALUES[id];
    }

    public static LatencyMetrics[] getValues(){
        return VALUES;
    }

    public int getId() {
        return id;
    }

    public static LatencyPoint init(String event){
        return new LatencyPoint(event);
    }
}
