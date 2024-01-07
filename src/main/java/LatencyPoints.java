import java.util.HashMap;
import java.util.Map;

/**
 * @author lzn
 * @date 2024/01/07 14:51
 * @description
 */
public class LatencyPoints {

    private static final ThreadLocal<Map<String, LatencyPoint>> POINTS = ThreadLocal.withInitial(HashMap::new);

    public static LatencyPoint getOrInstantiate(String event){
        Map<String, LatencyPoint> pointMap = POINTS.get();
        return pointMap.computeIfAbsent(event, LatencyPoint::init);
    }

    public static Map<String, LatencyPoint> getLatencyMap(){
        return POINTS.get();
    }
}
