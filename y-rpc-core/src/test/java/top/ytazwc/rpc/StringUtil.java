package top.ytazwc.rpc;

/**
 * @author 00103943
 * @date 2025-04-01 15:20
 * @package top.ytazwc.rpc
 * @description
 */
public class StringUtil {

    public static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

}
