package core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzn
 * @date 2024/01/27 18:00
 * @description Global context of latency point
 */
@Slf4j
public class LatencyPointContext {

    public final static LatencyPointContext INSTANCE = new LatencyPointContext();

    @Getter
    public ClientInfo clientInfo;

    private LatencyPointContext() {
        clientInfo = new ClientInfo()
                .hostIp(System.getProperty("client.host.ip", "localhost"))
                .clientName(System.getProperty("client.name", "Undefined"))
                .instanceName(System.getProperty("instance.name", "Undefined"))
                .startTime(System.currentTimeMillis());

        log.info("Client initialized: {}", clientInfo);
    }
}
