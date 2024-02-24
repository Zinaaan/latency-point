import core.TracePoint;
import core.LatencyMetricMeter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lzn
 * @date 2024/01/06 22:34
 * @description Functional test for Latency point
 */
class LatencyMetricMeterTest {

    private final List<Integer> list = new CopyOnWriteArrayList<>();

    @Test
    void multiple_events_works_successful_for_single_thread() {
        TracePoint e2e = LatencyMetricMeter.tracePoint("e2e");
        TracePoint child1 = e2e.createChild("child1");
        TracePoint child2 = e2e.createChild("child2");
        e2e.start();

        for (int i = 0; i < 100; i++) {
            child1.start();
            list.add(i);
            child1.stop();
        }

        for (int i = 0; i < 50; i++) {
            child2.start();
            list.add(i);
            child2.stop();
        }

        e2e.stop();
        Assertions.assertEquals(1, LatencyMetricMeter.getLatencyMap().size());
        Assertions.assertTrue(e2e.hasChild());
    }

    @Test
    void multiple_events_works_successful_for_multiple_threads() {
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int n = 0; n < threadCount; n++) {
            executorService.execute(() -> {
                TracePoint e2e = LatencyMetricMeter.tracePoint("e2e");
                TracePoint child1 = e2e.createChild("child1");
                TracePoint child2 = e2e.createChild("child2");
                e2e.start();
                for (int i = 0; i < 100; i++) {
                    child1.start();
                    list.add(i);
                    child1.stop();
                }

                for (int i = 0; i < 50; i++) {
                    child2.start();
                    list.add(i);
                    child2.stop();
                }

                e2e.stop();
                latch.countDown();
                Assertions.assertNotNull(e2e);
                Assertions.assertNotNull(child1);
                Assertions.assertNotNull(child2);
                Assertions.assertTrue(e2e.hasChild());
                Assertions.assertEquals(1, LatencyMetricMeter.getLatencyMap().size());
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}