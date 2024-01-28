package core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.MetricBuffer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Map;

/**
 * @author lzn
 * @date 2024/01/27 17:34
 * @description Wrapped data container includes client identification info and metrics
 */
@Data
public class MetricDataWrapper {

    private ClientInfo clientInfo;

    private long reportTime;

    @JsonIgnore
    private Map<MetricBuffer, LatencyPoint> aggMetrics;

    private Collection<LatencyPoint> latencyPoints;

    public MetricDataWrapper(ClientInfo clientInfo, Map<MetricBuffer, LatencyPoint> aggMetrics) {
        this.clientInfo = clientInfo;
        this.aggMetrics = aggMetrics;
        latencyPoints = aggMetrics.values();
    }

    public Key getKey(LatencyPoint point) {
        return new Key(clientInfo.hostIp(), clientInfo.clientName(), clientInfo.instanceName(), point);
    }

    public MetricDataWrapper clientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
        return this;
    }

    @Data
    @AllArgsConstructor
    @Accessors(fluent = true)
    private static class Key {
        private String hostIp;
        private String clientName;
        private String instanceName;
        private LatencyPoint point;
    }
}
