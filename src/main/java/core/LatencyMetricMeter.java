package core;

import org.agrona.collections.Object2ObjectHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * @author lzn
 * @date 2024/01/07 14:51
 * @description Global latency points distributor
 */
public class LatencyMetricMeter {

    private static final ThreadLocal<Object2ObjectHashMap<String, TracePoint>> POINTS = ThreadLocal.withInitial(Object2ObjectHashMap::new);

    public static TracePoint tracePoint(String blockName) {
        return getOrInstantiate(blockName, "reqId");
    }

    private static TracePoint getOrInstantiate(String blockName, String reqId) {
        Map<String, TracePoint> pointMap = POINTS.get();
        return Optional.ofNullable(pointMap.get(blockName)).orElseGet(() -> {
            TracePoint point = new TracePoint(blockName, reqId, "-1");
            pointMap.put(blockName, point);
            return point;
        });
    }

    public static Map<String, TracePoint> getLatencyMap() {
        return POINTS.get();
    }
}
