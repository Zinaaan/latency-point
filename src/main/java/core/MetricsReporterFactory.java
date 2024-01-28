package core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author lzn
 * @date 2024/01/27 17:03
 * @description Factory of metric reporter allocation
 */
@Component
@Slf4j
public class MetricsReporterFactory {

    private static MetricsReporter reporter;

    private final MeterMetricReporter metricReporter;

    private MetricsReporterFactory(MeterMetricReporter metricReporter) {
        this.metricReporter = metricReporter;
    }

    @PostConstruct
    private void init() {
        if (metricReporter != null) {
            log.info("Specify metric reporter to be MeterMetricReporter");
            reporter = metricReporter;
        }
    }

    public static MetricsReporter getReporter() {
        if (reporter == null) {
            reporter = new RemoteMetricReporter();
        }

        return reporter;
    }
}
