import org.agrona.collections.Object2ObjectHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * @author lzn
 * @date 2024/01/07 14:51
 * @description Global latency points distributor
 */
public class LatencyPointsDistributor {

    private static final ThreadLocal<Object2ObjectHashMap<String, LatencyPoint>> POINTS = ThreadLocal.withInitial(Object2ObjectHashMap::new);

    public static LatencyPoint latencyPoint(String event){
        return getOrInstantiate(event, PointType.LATENCY);
    }

    public static LatencyPoint countPoint(String event){
        return getOrInstantiate(event, PointType.COUNT);
    }

    public static LatencyPoint gaugePoint(String event){
        return getOrInstantiate(event, PointType.GAUGE);
    }

    private static LatencyPoint getOrInstantiate(String event, PointType type){
        Map<String, LatencyPoint> pointMap = POINTS.get();
        return Optional.ofNullable(pointMap.get(event)).orElseGet(() -> {
            LatencyPoint point = new LatencyPoint(event, type);
            pointMap.put(event, point);
            return point;
        });
    }

    public static Map<String, LatencyPoint> getLatencyMap(){
        return POINTS.get();
    }
}
