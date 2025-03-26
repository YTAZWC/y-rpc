package top.ytazwc.rpc.compress.gzip;

import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.compress.Compress;
import top.ytazwc.rpc.exception.CompressException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author 00103943
 * @date 2025-03-26 15:52
 * @package top.ytazwc.rpc.compress.gzip
 * @description 使用 gzip 算法实现数据压缩解压缩
 */
@Slf4j
public class GzipCompress implements Compress {

    // 解压缓存区大小
    private static final int BUFFER_SIZE = 1024 * 4;

    @Override
    public byte[] compress(byte[] bytes) {
        checkData(bytes);
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(out);
        ) {
            gzipOut.write(bytes);
            gzipOut.flush();
            gzipOut.finish();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("gzip 压缩数据: [{}] 失败!", Arrays.toString(bytes));
            throw new CompressException("gzip 压缩数据失败!!!", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        checkData(bytes);

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPInputStream gzipInput = new GZIPInputStream(new ByteArrayInputStream(bytes));
        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            // 读取并解压
            while ((n = gzipInput.read(buffer)) > -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            log.error("gzip 解压数据: [{}] 失败!", Arrays.toString(bytes));
            throw new CompressException("gzip 解压数据失败!!!", e);
        }

    }

    /**
     * 检查数据是否为null
     * @param bytes 待检查数据
     */
    private static void checkData(byte[] bytes) {
        if (Objects.isNull(bytes)) {
            log.error("需要压缩数据为 null !");
            throw new NullPointerException("待压缩数据为 null !");
        }
    }

}
