package top.ytazwc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 00103943
 * @date 2025-03-18 15:59
 * @package top.ytazwc.rpc.enums
 * @Description rpc调用响应状态类型
 */
@Getter
@ToString
@AllArgsConstructor
public enum RpcResCode {

    SUCCESS(200, "远程调用成功!"),
    FAIL(500, "远程调用失败!!!"),
    ;

    // 响应码
    private final int code;
    // 响应码对应响应信息
    private final String message;

}
