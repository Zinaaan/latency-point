package core;

import common.PointType;
import org.agrona.collections.Object2ObjectHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * @author lzn
 * @date 2024/01/07 14:51
 * @description Global latency points distributor
 */
public class LatencyMetricMeter {

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
            LatencyPoint point = new LatencyPoint(Thread.currentThread().getName(), blockName, type);
            pointMap.put(blockName, point);
            MetricManager.INSTANCE.addLatencyPoint(point);
            return point;
        });
    }

    public static Map<String, LatencyPoint> getLatencyMap(){
        return POINTS.get();
    }
}
