package top.ytazwc.rpc.config;

import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.enums.RpcProperty;
import top.ytazwc.rpc.exception.RpcException;
import top.ytazwc.rpc.utils.PropertiesFileUtil;

import java.util.Objects;
import java.util.Properties;

/**
 * @author 00103943
 * @date 2025-03-21 17:38
 * @package top.ytazwc.rpc.utils
 * @Description 网络工具 管理端口
 */
@Slf4j
@NoArgsConstructor
public class RpcConfig {

    // 配置文件名
    private static final String RPC_PROPERTIES = "rpc.properties";
    // 默认端口
    private static final int DEFAULT_PORT = 9988;
    // 默认服务注册中心


    /**
     * 获取服务提供者端口
     * @return 服务提供者 服务注册端口
     */
    public static int getPort() {
        String property = getProperty(RpcProperty.PROVIDER_PORT.getValue());
        if (Objects.isNull(property)) {
            return DEFAULT_PORT;
        }
        if (!StrUtil.isNumeric(property)) {
            throw new RpcException("服务提供者端口不是Integer类型!!!");
        }
        int port = Integer.parseInt(property);

        if (port < 0) {
            throw new RpcException("服务提供者端口为负数!!!");
        }

        return port;
    }

    /**
     * 获取配置文件中的参数
     * @param key 参数对应的key
     * @return 返回配置文件中的参数
     */
    private static String getProperty(String key) {
        Properties properties = PropertiesFileUtil.readProperties(RPC_PROPERTIES);
        if (Objects.nonNull(properties) && Objects.nonNull(properties.getProperty(key))) {
            return properties.getProperty(key);
        }
        return null;
    }


}
