package core;

import common.MetricBuffer;
import common.PointType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private final MetricDataWrapper metricDataWrapper;

    private final Map<MetricBuffer, LatencyPoint> aggMetrics = new ConcurrentHashMap<>();

    public RemoteMetricReporter() {
        this.metricDataWrapper = new MetricDataWrapper(LatencyPointContext.INSTANCE.getClientInfo(), aggMetrics);
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
        if (!aggMetrics.isEmpty()) {
            MetricManager.INSTANCE.startCollection();
            // Send metrics to the endpoint
//            remoteConnector.reportMetrics(metricDataWrapper.setReportTime(System.currentTimeMillis()));
        }

        MetricManager.INSTANCE.stopCollection();
        aggMetrics.values().forEach(LatencyPoint::clear);
    }

    @Override
    public void report(MetricBuffer buf) {
        LatencyPoint point = aggMetrics.get(buf);
        if (point.type() == PointType.LATENCY) {
            point.compute(buf.getLatency());
        }
    }

    public Map<MetricBuffer, LatencyPoint> getAggMetrics() {
        return aggMetrics;
    }
}
