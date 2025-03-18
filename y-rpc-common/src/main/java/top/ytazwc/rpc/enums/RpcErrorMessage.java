package top.ytazwc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 00103943
 * @date 2025-03-18 17:15
 * @package top.ytazwc.rpc.enums
 * @Description rpc 错误信息枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum RpcErrorMessage {

    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务失败!"),
    SERVER_INVOCATION_FAILURE("服务调用失败!"),
    SERVICE_NOT_FOUND("没有找到指定服务!"),
    REQUEST_NOT_MATCH_RESPONSE("错误返回结果!请求和响应不匹配!"),
    ;

    private final String message;

}
