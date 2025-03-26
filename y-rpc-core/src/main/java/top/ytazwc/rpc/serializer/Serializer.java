package top.ytazwc.rpc.serializer;

/**
 * @author 00103943
 * @date 2025-03-26 16:28
 * @package top.ytazwc.rpc.serializer
 * @description 自定义序列化接口
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj 待序列化 实例
     * @return 序列化结果
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 待反序列化数据
     * @param clazz 反序列化目标类型
     * @return 反序列化结果
     * @param <T> 目标类型
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
