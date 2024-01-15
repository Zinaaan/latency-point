import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author lzn
 * @date 2024/01/14 22:19
 * @description Processing the metrics of latency point and send to the endpoint
 */
public class LatencyPointProcessor implements Runnable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(LatencyPointProcessor.class);

    private boolean isRunning = true;

    @Override
    public void close() throws IOException {
        try {
            while (isRunning){
                sendMetrics();
            }
        } catch (Exception e) {
            LOG.info("Error on sending metrics: [{}]", e.toString());
        }
    }

    private void sendMetrics() {
        if(!LatencyPointAggregator.INSTANCE.latencyMap.isEmpty()){
            LatencyPointAggregator.INSTANCE.startCollection();
            // Send metrics to the endpoint
        }

        LatencyPointAggregator.INSTANCE.stopCollection();
    }

    @Override
    public void run() {
        isRunning = false;
    }
}
