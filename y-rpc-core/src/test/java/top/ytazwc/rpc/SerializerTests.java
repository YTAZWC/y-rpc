package top.ytazwc.rpc;

import org.junit.Test;
import top.ytazwc.rpc.compress.Compress;
import top.ytazwc.rpc.compress.gzip.GzipCompress;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.serializer.Serializer;
import top.ytazwc.rpc.serializer.kryo.KryoSerializer;
import top.ytazwc.rpc.service.TestService;
import top.ytazwc.rpc.service.impl.TestServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

/**
 * @author 00103943
 * @date 2025-03-26 17:22
 * @package top.ytazwc.rpc
 * @description
 */
public class SerializerTests {

    @Test
    public void testKryoSerializer() throws IOException {
        Serializer serializer = new KryoSerializer();
        Compress compress = new GzipCompress();
        TestService testService = new TestServiceImpl();
//        RpcRequest request = RpcRequest.builder()
//                .version("version")
//                .group("group")
//                .requestId(UUID.randomUUID().toString())
//                .interfaceName(testService.getClass().getInterfaces()[0].getName())
//                .paramTypes(new Class[]{String.class, String.class})
//                .parameters(new Object[]{"String", "String"})
//                .build();

        RpcRequest request = RpcRequest.builder().methodName("hello")
                .parameters(new Object[]{"sayheloossssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssloo", "sayhelooloosayhelooloo"})
                .interfaceName("top.ytazwc.rpc")
                .paramTypes(new Class<?>[]{String.class, String.class})
                .requestId(UUID.randomUUID().toString())
                .group("group1")
                .version("version1")
                .build();

        // Java 自带序列化
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(request);
        byte[] byteArray = out.toByteArray();

        // 序列化之前 实体
        System.out.println(request);

        // 序列化之后 数据大小
        System.out.println("Java 序列化之后, 数据大小: " + byteArray.length);

        // 进行序列化
        byte[] kryoByte = serializer.serialize(request);
        // 对 kryo 序列化之后数据 进一步压缩
        byte[] gzipByte = compress.compress(kryoByte);
        // kryo 序列化之后 数据大小
        System.out.println("kryo 序列化之后, 数据大小: " + kryoByte.length);
        System.out.println("进一步压缩后数据大小: " + gzipByte.length);
        // 反序列化结果
        RpcRequest deserialize = serializer.deserialize(kryoByte, RpcRequest.class);
        System.out.println(deserialize);


    }

}
