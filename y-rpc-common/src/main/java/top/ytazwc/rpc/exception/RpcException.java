package top.ytazwc.rpc.exception;

import top.ytazwc.rpc.enums.RpcErrorMessage;

/**
 * @author 00103943
 * @date 2025-03-18 17:13
 * @package top.ytazwc.rpc.exception
 * @Description rpc 调用异常类
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(RpcErrorMessage message, String detail) {
        this(message.getMessage() + " : " + detail);
    }

    public RpcException(RpcErrorMessage message) {
        this(message.getMessage());
    }

}
