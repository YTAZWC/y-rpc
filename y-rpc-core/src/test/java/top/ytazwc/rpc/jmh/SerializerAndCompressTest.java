package top.ytazwc.rpc.jmh;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import top.ytazwc.rpc.compress.Compress;
import top.ytazwc.rpc.compress.gzip.GzipCompress;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.serializer.Serializer;
import top.ytazwc.rpc.serializer.kryo.KryoSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author 00103943
 * @date 2025-04-01 14:26
 * @package top.ytazwc.rpc.jmh
 * @description
 */
@Slf4j
public class SerializerAndCompressTest {

    RpcRequest request;
    Serializer serializer;
    Compress compress;

    @Before
    public void before() {
//        request = RpcRequest.builder()
//                .version("v1")       // 2字符 (ASCII编码占2字节)
//                .group("g")          // 1字符 (1字节)
//                .requestId("0x1234") // 6字符 (6字节) 使用16进制简写替代UUID
//                .interfaceName("S")  // 1字符 (1字节) 使用单字母接口名
//                .paramTypes(new Class<?>[]{byte.class}) // 基本类型byte (类型标记1字节)
//                .parameters(new Object[]{(byte)0x1})    // byte值 (1字节)
//                .build();
//        request = RpcRequest.builder()
//                .version(StringUtil.repeat("v", 20))             // 20字符 × 2字节 = 40字节 (UTF-16)
//                .group(StringUtil.repeat("g", 15))               // 15字符 × 2字节 = 30字节
//                .requestId(UUID.randomUUID().toString()) // 36字符 × 2字节 = 72字节
//                .interfaceName(StringUtil.repeat("ServiceImpl", 2))  // 22字符 × 2 = 44字节（实际截断到需要字节）
//                .paramTypes(new Class<?>[]{byte.class, byte.class, byte.class, byte.class, byte.class}) // 数组头12字节 + 5×4 = 32字节
//                .parameters(new Object[]{(byte)1, (byte)2, (byte)3, (byte)4, (byte)5}) // 数组头12字节 + 5×1 = 17字节 (对齐填充后)
//                .build();

        // 1
//        request = new RpcRequest(
//                "", "A", "a", new Object[0], new Class[0], "", ""
//        );
        // 2
//        request = new RpcRequest(
//                "req1", "UserService", "getUser",
//                new Object[]{1}, new Class[]{int.class}, "1.0", "groupA"
//        );

        // 3
//        request = new RpcRequest(
//                "req2", "OrderService", "create",
//                new Object[]{"ID-123"}, new Class[]{String.class}, "2.0", "trade"
//        );

        // 4
//        request = new RpcRequest(
//                "req3", "DataService", "batchUpdate",
//                new Object[]{Arrays.asList(1,2,3)},
//                new Class[]{List.class}, "1.2", "data"
//        );

        // 5
//        request = new RpcRequest(
//                "req4", "ImageService", "upload",
//                new Object[]{new byte[1024]},
//                new Class[]{byte[].class}, "3.0", "media"
//        );

        // 6
//        request = new RpcRequest(
//                "req5", "ComplexService", "process",
//                new Object[]{new User(5, "测试用户")},
//                new Class[]{User.class}, "2.1", "system"
//        );

        // 7
//        request = new RpcRequest(
//                "req6", "MixService", "execute",
//                new Object[]{1, "参数", LocalDateTime.now()},
//                new Class[]{int.class, String.class, LocalDateTime.class},
//                "4.0", "mixed"
//        );

        // 8
//        request = new RpcRequest(
//                "req7", "CacheService", "refresh",
//                new Object[]{StringUtil.repeat("ABCDEFGHIJ", 10)},
//                new Class[]{String.class}, "5.0", "cache"
//        );

        // 9
        // 创建可序列化的标准 Map
//        Map<String, Object> configMap = new HashMap<>();
//        configMap.put("key1", 1);
//        configMap.put("key2", "value");
//
//        request = new RpcRequest(
//                "req8",
//                "ConfigService",
//                "update",
//                new Object[]{configMap},          // 使用标准 HashMap
//                new Class<?>[]{Map.class},        // 保留接口类型
//                "2.3",
//                "config"
//        );

        // 10
        request = new RpcRequest(
                "req9", "BigDataService", "analyze",
                new Object[]{new String(new byte[10_000])},
                new Class[]{String.class}, "6.0", "bigdata"
        );


        String layout = ClassLayout.parseInstance(request).toPrintable();
//        log.info("对象内存布局:\n{}", layout);
        // 获取总占用字节数
        long size = ClassLayout.parseInstance(request).instanceSize();
//        log.info("序列化前,占用字节: [{}]", size);
        log.info("完整内存占用: [{}] bytes",
                GraphLayout.parseInstance(request).totalSize());

//        log.info("字节数: [{}]", request.toString().getBytes(StandardCharsets.UTF_8).length);

        serializer = new KryoSerializer();
        compress = new GzipCompress();
    }

    @Test
    public void testKryo() {
        byte[] serialize = serializer.serialize(request);
        log.info("kryo序列化后,占用字节: [{}]", serialize.length);
    }

    @Test
    public void testJava() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(request);
        byte[] byteArray = out.toByteArray();
        log.info("Java 序列化后,占用字节: [{}]", byteArray.length);
    }

    @Test
    public void testKryoAndGzip() {
        byte[] serialize = serializer.serialize(request);
        byte[] bytes = compress.compress(serialize);
        log.info("kryo序列化+gzip压缩后后,占用字节: [{}]", bytes.length);
    }



}
