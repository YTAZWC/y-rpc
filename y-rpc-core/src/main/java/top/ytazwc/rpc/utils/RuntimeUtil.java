package top.ytazwc.rpc.utils;

/**
 * @author 00103943
 * @date 2025-03-28 16:32
 * @package top.ytazwc.rpc.utils
 * @description
 */
public class RuntimeUtil {

    /**
     * 获取 CPU 核心数
     * @return cpu的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }

}
