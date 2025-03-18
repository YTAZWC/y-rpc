package top.ytazwc.rpc.entity;

import lombok.*;
import top.ytazwc.rpc.enums.RpcResCode;

import java.io.Serializable;

/**
 * @author 00103943
 * @date 2025-03-18 15:50
 * @package top.ytazwc.rpc.entity
 * @Description rpc 请求响应类
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {

    // 序列化版本
    private static final long serialVersionUID = 1L;

    // 请求id
    private String requestId;

    // 响应码
    private Integer code;

    // 响应信息
    private String message;

    // 响应数据
    private T data;

    /**
     * 响应成功
     * @param data 响应具体数据
     * @param requestId 响应对应的请求id
     * @return  返回具体响应实例
     * @param <T>   响应数据类型
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResCode.SUCCESS.getCode());
        response.setMessage(RpcResCode.SUCCESS.getMessage());
        response.setRequestId(requestId);
        response.setData(data);

        return response;
    }

    /**
     * 响应失败
     * @param requestId 响应对应请求id
     * @return  返回失败响应结果
     * @param <T> 实体数据类型
     */
    public static <T> RpcResponse<T> fail(String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResCode.FAIL.getCode());
        response.setMessage(RpcResCode.FAIL.getMessage());
        response.setRequestId(requestId);

        return response;
    }

}
