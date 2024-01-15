import org.agrona.collections.Object2ObjectHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * @author lzn
 * @date 2024/01/07 14:51
 * @description Global latency points distributor
 */
public class LatencyPointsDistributor {

    private static final Map<String, Map<String, LatencyPoint>> GLOBAL_LATENCY_POINT_MAP = new Object2ObjectHashMap<>();
    private static final ThreadLocal<Object2ObjectHashMap<String, LatencyPoint>> POINTS = ThreadLocal.withInitial(Object2ObjectHashMap::new);

    public static LatencyPoint latencyPoint(String blockName){
        return getOrInstantiate(blockName, PointType.LATENCY);
    }

    public static LatencyPoint countPoint(String blockName){
        return getOrInstantiate(blockName, PointType.COUNT);
    }

    public static LatencyPoint gaugePoint(String blockName){
        return getOrInstantiate(blockName, PointType.GAUGE);
    }

    private static LatencyPoint getOrInstantiate(String blockName, PointType type){
        Map<String, LatencyPoint> pointMap = POINTS.get();
        return Optional.ofNullable(pointMap.get(blockName)).orElseGet(() -> {
            LatencyPoint point = new LatencyPoint(blockName, type);
            pointMap.put(blockName, point);
            String threadName = Thread.currentThread().getName();
            GLOBAL_LATENCY_POINT_MAP.put(threadName, pointMap);
            LatencyPointAggregator.INSTANCE.addClientInfo(threadName, blockName, type);
            return point;
        });
    }

    public static Map<String, LatencyPoint> getLatencyMap(){
        return POINTS.get();
    }

    public static Map<String, Map<String, LatencyPoint>> getGlobalLatencyPointMap(){
        return GLOBAL_LATENCY_POINT_MAP;
    }
}
