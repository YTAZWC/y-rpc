package top.ytazwc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 00103943
 * @date 2025-03-20 17:16
 * @package top.ytazwc.rpc.enums
 * @Description rpc 配置文件相关属性
 */
@AllArgsConstructor
@Getter
public enum RpcProperty {

    ZK_ADDRESS("rpc.zookeeper.address"),
    PROVIDER_PORT("rpc.provider.port"),
    ;

    private final String value;

}
