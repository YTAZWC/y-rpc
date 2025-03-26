package top.ytazwc.rpc.compress;

/**
 * @author 00103943
 * @date 2025-03-26 15:49
 * @package top.ytazwc.rpc.compress
 * @description 数据压缩接口 对二进制数据进一步压缩 提高网络传输效率
 */
public interface Compress {

    /**
     * 压缩数据
     * @param bytes 需要压缩的二进制数据
     * @return 压缩后的数据
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压
     * @param bytes 压缩的数据
     * @return 解压后的数据
     */
    byte[] decompress(byte[] bytes);

}
