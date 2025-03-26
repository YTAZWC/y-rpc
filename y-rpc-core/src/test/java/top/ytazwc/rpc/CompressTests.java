package top.ytazwc.rpc;

import org.junit.Test;
import top.ytazwc.rpc.compress.Compress;
import top.ytazwc.rpc.compress.gzip.GzipCompress;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.service.TestService;
import top.ytazwc.rpc.service.impl.TestServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author 00103943
 * @date 2025-03-26 16:15
 * @package top.ytazwc.rpc
 * @description 测试压缩算法
 */
public class CompressTests {

    @Test
    public void testGzip() throws IOException {
        Compress compress = new GzipCompress();
        TestService testService = new TestServiceImpl();
        RpcRequest request = RpcRequest.builder()
                .version("version")
                .group("group")
                .requestId(UUID.randomUUID().toString())
                .interfaceName(testService.getClass().getInterfaces()[0].getName())
                .paramTypes(new Class[]{})
                .parameters(new Object[]{})
                .build();

        // 序列化后压缩
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(request);

        byte[] byteArray = out.toByteArray();
        byte[] compressByte = compress.compress(byteArray);
        byte[] decompressByte = compress.decompress(compressByte);

        // 判断压缩和解压后是否一致
        System.out.printf("压缩和解压缩后是否一致: " + Arrays.equals(byteArray, decompressByte));
        System.out.println("压缩前,序列化结果: " + Arrays.toString(byteArray));
        System.out.println("压缩后,压缩结果: " + Arrays.toString(compressByte));
        System.out.println("解压后,解压结果: " + Arrays.toString(decompressByte));

        System.out.println("压缩前, 大小为: " + byteArray.length);
        System.out.println("压缩后, 大小为: " + compressByte.length);


    }

}
