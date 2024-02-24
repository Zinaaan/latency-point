package core;

import common.TraceBuffer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author lzn
 * @date 2024/01/27 16:53
 * @description Report and aggregate metrics locally and send metrics out to remote endpoints
 */
@Slf4j
public class RemoteMetricReporter implements MetricsReporter, Runnable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteMetricReporter.class);

    private boolean isRunning = true;

//    private final MetricDataWrapper metricDataWrapper;

    public RemoteMetricReporter() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 10, Long.parseLong(System.getProperty("metric.report.remote.period", "10")), TimeUnit.SECONDS);
        log.info("Remote Metric Reporter has been initialized");
    }

    @Override
    public void run() {
        try {
            if (isRunning) {
                sendMetrics();
            }
        } catch (Exception e) {
            LOG.info("Error on sending metrics: [{}]", e.toString());
        }
    }

    @Override
    public void close() {
        isRunning = false;
    }

    private void sendMetrics() {
        MetricManager.INSTANCE.startReporting();
        // Send metrics to the endpoint
//            remoteConnector.reportMetrics(metricDataWrapper.setReportTime(System.currentTimeMillis()));
//        }
        MetricManager.INSTANCE.stopReporting();
    }

    @Override
    public void report(TraceBuffer buf) {
        // Skip first byte
        buf.setLength(1);
        int offset = 1, len = buf.getByte(0);
        while (offset < len) {
            String requestId = buf.getRequestId();
            String blockName = buf.getBlockName();
            String parentName = buf.getParentName();
            long latency = buf.getLatency();
            log.info("Request id: {}, block name: {}, parent: {}, latency: {}", requestId, blockName, parentName, latency);
            offset = buf.getLength();
        }
    }
}
