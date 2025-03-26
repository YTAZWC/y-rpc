package top.ytazwc.rpc.exception;

/**
 * @author 00103943
 * @date 2025-03-26 16:55
 * @package top.ytazwc.rpc.exception
 * @description 序列化异常
 */
public class SerializerException extends RuntimeException {

    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(String message, Throwable e) {
        super(message, e);
    }

}
