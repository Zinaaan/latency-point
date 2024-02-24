package core;

import common.TraceBuffer;

/**
 * @author lzn
 * @date 2024/01/17 23:02
 * @description Report metrics either to the Meter registry or remote
 */
public interface MetricsReporter {

    /**
     * Report metrics
     *
     * @param buf Metrics data in bytes
     */
    void report(TraceBuffer buf);
}
