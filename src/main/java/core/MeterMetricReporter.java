package core;

import common.TraceBuffer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Component;

/**
 * @author lzn
 * @date 2024/01/27 16:53
 * @description Report metrics to Meter registry to enable Prometheus scrape metrics and shows in Grafana
 */
@Component
@AutoConfigureAfter(MeterRegistry.class)
public class MeterMetricReporter implements MetricsReporter {

    private final MeterRegistry registry;

    public MeterMetricReporter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void report(TraceBuffer buf) {

    }
}
