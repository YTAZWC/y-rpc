package top.ytazwc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 00103943
 * @date 2025-03-27 13:55
 * @package top.ytazwc.rpc.enums
 * @description 序列化类型
 */
@Getter
@ToString
@AllArgsConstructor
public enum SerializerType {

    KRYO((byte)0x01, "kryo");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        SerializerType[] values = SerializerType.values();
        for (SerializerType value : values) {
            if (value.getCode() == code) {
                return value.getName();
            }
        }
        return null;
    }

}
