package top.ytazwc.rpc.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.entity.RpcResponse;
import top.ytazwc.rpc.exception.SerializerException;
import top.ytazwc.rpc.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 00103943
 * @date 2025-03-26 16:43
 * @package top.ytazwc.rpc.serializer.kryo
 * @description 使用 kryo 实现序列化和反序列化
 */
@Slf4j
public class KryoSerializer implements Serializer {

    // 由于 kryo 不是线程安全的， 所以使用 ThreadLocal 对 kryo 对象实例 进行存储
    // 为每个线程 创建一个 kryo 对象用于序列化
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 注册需要序列化和反序列化的类
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {

        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Output output = new Output(outputStream);
        ) {
            Kryo kryo = kryoThreadLocal.get();
            // 将对象序列化为 byte 数组
            kryo.writeObject(output, obj);
            output.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("序列化失败!");
            throw new SerializerException("序列化失败!", e);
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(inputStream)
        ) {
            Kryo kryo = kryoThreadLocal.get();
            // 进行反序列化
            return kryo.readObject(input, clazz);
        } catch (IOException e) {
            log.error("反序列化失败!");
            throw new SerializerException("反序列化失败!", e);
        }
    }

}
