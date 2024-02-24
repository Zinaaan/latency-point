import core.LatencyMetricMeter;
import core.TracePoint;

import java.util.concurrent.TimeUnit;

public class ProcessTest {
    public static void main(String[] args) {
        TracePoint parent = LatencyMetricMeter.tracePoint("parent");
        TracePoint child1 = parent.createChild("child1");
        TracePoint child2 = parent.createChild("child2");
        TracePoint child2_1 = child2.createChild("child2_1");
        while (true){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            parent.start();
            child1.start();
            child2.start();
            child2_1.start();
            child2_1.stop();
            child2.stop();
            child1.stop();
            parent.stop();
        }
    }

}