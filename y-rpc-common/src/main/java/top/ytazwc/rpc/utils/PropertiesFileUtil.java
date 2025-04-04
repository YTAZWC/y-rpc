package top.ytazwc.rpc.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 00103943
 * @date 2025-03-20 16:24
 * @package top.ytazwc.rpc.utils
 * @Description 读取配置文件工具类
 */
@Slf4j
@NoArgsConstructor
public class PropertiesFileUtil {

    // 配置文件读取缓存
    private static final Map<String, Properties> PROPERTIES_MAP = new ConcurrentHashMap<>();

    /**
     * 读取配置文件
     * @param fileName 文件名
     * @return 配置信息类实例
     */
    public static Properties readProperties(String fileName) {

        // 已经加载过对应配置
        if (PROPERTIES_MAP.containsKey(fileName)) {
            return PROPERTIES_MAP.get(fileName);
        }

        Properties properties = null;
        // 加载配置文件
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        // rpc 配置路径
        String rpcConfigPath = "";
        if (Objects.nonNull(url)) {
            rpcConfigPath = url.getPath() + fileName;
        }
        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)
        ) {
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            log.error("读取配置文件: [{}] 出错!!!", fileName);
        }
        PROPERTIES_MAP.put(fileName, properties);
        return properties;
    }

}
