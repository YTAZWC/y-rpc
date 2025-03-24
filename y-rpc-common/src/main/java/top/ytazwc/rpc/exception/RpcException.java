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

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessage message, String detail) {
        super(message.getMessage() + " : " + detail);
    }

    public RpcException(RpcErrorMessage message) {
        super(message.getMessage());
    }

}
