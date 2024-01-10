import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author lzn
 * @date 2024/01/07 14:51
 * @description
 */
public class LatencyPointsDistributor {

    private static final ThreadLocal<Map<String, LatencyPoint>> POINTS = ThreadLocal.withInitial(HashMap::new);

    public static LatencyPoint getOrInstantiate(String event){
        Map<String, LatencyPoint> pointMap = POINTS.get();
        return Optional.ofNullable(pointMap.get(event)).orElseGet(() -> {
            LatencyPoint point = LatencyPoint.init(event);
            pointMap.put(event, point);
            return point;
        });
    }

    public static Map<String, LatencyPoint> getLatencyMap(){
        return POINTS.get();
    }
}
