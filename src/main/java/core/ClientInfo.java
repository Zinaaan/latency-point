package core;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lzn
 * @date 2024/01/27 17:34
 * @description Client identification info
 */
@Data
@Accessors(fluent = true)
public class ClientInfo {

    private String hostName;
    private String hostIp;
    private String clientName;
    private String instanceName;
    private long startTime;
}
