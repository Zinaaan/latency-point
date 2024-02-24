package core;

import common.TraceBuffer;
import net.openhft.chronicle.bytes.Bytes;

import java.util.HashSet;
import java.util.Set;

import static core.MetricManager.collect;

/**
 * @author lzn
 * @date 2024/02/18 14:59
 * @description
 */
public class TracePoint {
    private final TraceBuffer buffer;
    private TracePoint parent;
    private boolean isRoot = true;
    private final String blockName;
    private final String reqId;
    private long startTime;
    private final Set<String> children = new HashSet<>();
    private final Bytes<byte[]> metricBytes;

    public boolean hasChild() {
        return children.size() > 0;
    }

    public TracePoint(String blockName, String reqId, String parentName) {
        this.blockName = blockName;
        this.reqId = reqId;
        parent = null;
        this.buffer = new TraceBuffer(3 + reqId.length() + blockName.length() + parentName.length() + Long.BYTES).putRequestId(reqId).putBlockName(blockName).putParentName(parentName).putLatency(0L);
        metricBytes = Bytes.allocateElasticOnHeap(100);
    }

    public TracePoint createChild(String blockName) {
        if (!children.add(blockName)) {
            throw new RuntimeException("Duplicate child");
        }
        TracePoint child = new TracePoint(blockName, reqId, this.blockName);
        child.parent = this;
        child.isRoot = false;
        return child;
    }

    public TracePoint start() {
        startTime = System.nanoTime();
        return this;
    }

    public TracePoint stop() {
        long latency = System.nanoTime() - startTime;
        if (isRoot) {
            buffer.writeLatency(latency);
            collect(createMetrics());
        } else {
            buffer.writeLatency(latency);
            parent.buffer.wrap(buffer);
        }

        return this;
    }

    public TracePoint stop(Throwable throwable) {
        buffer.putLatency(System.nanoTime() - startTime);
        return this;
    }

    private byte[] createMetrics() {
        return metricBytes.clear().write(buffer.byteArray(), 0, buffer.getLength()).toByteArray();
    }

    @Override
    public String toString() {
        return "TracePoint{" +
                "parent=" + isRoot +
                ", blockName='" + blockName + '\'' +
                ", reqId='" + reqId + '\'' +
                ", buffer='" + buffer + '\'' +
                ", children=" + children +
                '}';
    }
}
