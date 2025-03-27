package top.ytazwc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 00103943
 * @date 2025-03-27 11:36
 * @package top.ytazwc.rpc.enums
 * @description 压缩类型
 */
@Getter
@ToString
@AllArgsConstructor
public enum CompressType {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        CompressType[] values = CompressType.values();
        for (CompressType value : values) {
            if (value.getCode() == code) {
                return value.getName();
            }
        }
        return null;
    }

}
