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
class LatencyPointsDistributorTest {

    private final List<Integer> list = new CopyOnWriteArrayList<>();

    @Test
    void multiple_events_works_successful_for_single_thread() {
        LatencyPoint e2e = LatencyPointsDistributor.latencyPoint("e2e");
        LatencyPoint step1 = LatencyPointsDistributor.countPoint("step1");
        LatencyPoint step2 = LatencyPointsDistributor.gaugePoint("step2");
        e2e.start();

        for (int i = 0; i < 100; i++) {
            step1.start();
            list.add(i);
            step1.stop();
        }

        for (int i = 0; i < 50; i++) {
            step2.start();
            list.add(i);
            step2.stop();
        }

        e2e.stop();

        Assertions.assertEquals(3, LatencyPointsDistributor.getLatencyMap().size());
        Assertions.assertEquals(1, e2e.getCount());
        Assertions.assertEquals(100, step1.getCount());
        Assertions.assertEquals(50, step2.getCount());
    }

    @Test
    void multiple_events_works_successful_for_multiple_threads() {
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int n = 0; n < threadCount; n++) {
            executorService.execute(() -> {
                LatencyPoint e2e = LatencyPointsDistributor.latencyPoint("e2e");
                LatencyPoint step1 = LatencyPointsDistributor.countPoint("step1");
                LatencyPoint step2 = LatencyPointsDistributor.gaugePoint("step2");
                e2e.start();
                for (int i = 0; i < 100; i++) {
                    step1.start();
                    list.add(i);
                    step1.stop();
                }

                for (int i = 0; i < 50; i++) {
                    step2.start();
                    list.add(i);
                    step2.stop();
                }

                e2e.stop();
                latch.countDown();
                Assertions.assertNotNull(e2e);
                Assertions.assertNotNull(step1);
                Assertions.assertNotNull(step2);
                Assertions.assertEquals(3, LatencyPointsDistributor.getLatencyMap().size());
                Assertions.assertEquals(1, e2e.getCount());
                Assertions.assertEquals(100, step1.getCount());
                Assertions.assertEquals(50, step2.getCount());
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