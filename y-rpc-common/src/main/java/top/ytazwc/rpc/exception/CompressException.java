package top.ytazwc.rpc.exception;

/**
 * @author 00103943
 * @date 2025-03-26 15:55
 * @package top.ytazwc.rpc.exception
 * @description 压缩、解压异常
 */
public class CompressException extends RuntimeException {

    public CompressException(String message) {
        super(message);
    }

    public CompressException(String message, Throwable e) {
        super(message, e);
    }

}
