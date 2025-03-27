package top.ytazwc.rpc.exception;

/**
 * @author 00103943
 * @date 2025-03-27 15:54
 * @package top.ytazwc.rpc.exception
 * @description rpc信息解码编码异常类
 */
public class MessageCoderException extends RuntimeException {
    public MessageCoderException(String message) {
        super(message);
    }

    public MessageCoderException(String message, Throwable e) {
        super(message, e);
    }

}
