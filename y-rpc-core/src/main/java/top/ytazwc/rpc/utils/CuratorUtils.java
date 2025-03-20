package top.ytazwc.rpc.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import top.ytazwc.rpc.enums.RpcProperty;
import top.ytazwc.rpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static top.ytazwc.rpc.constant.RpcConstant.RPC_PROPERTIES;

/**
 * @author 00103943
 * @date 2025-03-20 15:56
 * @package top.ytazwc.rpc.utils
 * @Description Java 操作 Zookeeper 工具类
 */
@Slf4j
@NoArgsConstructor
public class CuratorUtils {

    // 连接重试等待时间 毫秒
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大连接重试次数
    private static final int MAX_RETRIES = 3;
    // 服务注册 根节点
    public static final String ZK_ROOT_PATH = "/y-rpc";

    // 缓存服务注册信息 多种服务 相同服务多节点
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    // 注册服务信息集合
    private static final Set<String> REGISTER_SERVICE_PATH = ConcurrentHashMap.newKeySet();

    // 连接Zookeeper客户端
    private static CuratorFramework zkClient;
    // 默认 Zookeeper 连接地址
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    /**
     * 注册持久性节点： 持久化到Zookeeper中，不会因为 zk 重启而消失
     * @param client zk 客户端
     * @param path 注册节点路径
     */
    public static void addPersistentNode(CuratorFramework client, String path) {
        try {
            if (REGISTER_SERVICE_PATH.contains(path) || Objects.nonNull(client.checkExists().forPath(path))) {
                log.info("注册的节点已存在, 节点路径: [{}]", path);
            } else {
                // 路径规则为：/y-rpc/服务接口名/服务节点地址
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点注册成功, 节点路径: [{}]", path);
            }
            // 避免重复注册
            REGISTER_SERVICE_PATH.add(path);
        } catch (Exception e) {
            log.error("节点: [{}] 创建失败!!!", path);
        }
    }

    /**
     * 获取 Zookeeper 连接客户端
     * @return 返回 zk 客户端
     */
    public static CuratorFramework getZkClient() {
        // 检查是否有自定义 Zookeeper 服务地址
        Properties properties = PropertiesFileUtil.readProperties(RPC_PROPERTIES);
        // 确定 zk 服务地址
        String zkAddress = Objects.nonNull(properties) && Objects.nonNull(properties.getProperty(RpcProperty.ZK_ADDRESS.getValue())) ?
                properties.getProperty(RpcProperty.ZK_ADDRESS.getValue()) : DEFAULT_ZOOKEEPER_ADDRESS;

        // 如果客户端已经启动 则直接返回
        if (Objects.nonNull(zkClient) && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        // 确定客户端重试策略：指数增长重试策略
        RetryPolicy policy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        // 构建客户端
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .retryPolicy(policy)
                .build();
        // 启动
        zkClient.start();

        // 尝试等待 30 秒 确认是否连接到
        // 因为初始等待时间为 1000ms 为 1 s
        // 但是由于重试策略 等待时间指数增长
        // 第二次重试则是 1000 000 ms ；等待时间过于长
        try {
            // 阻塞 30 s 直到连接到 Zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                log.error("Zookeeper 连接超时!!!");
            }
        } catch (InterruptedException e) {
            throw new RpcException("阻塞等待连接Zookeeper出错!!!");
        }
        return zkClient;
    }


    /**
     * 获取服务下的子节点列表
     * @param client zk 客户端
     * @param serviceName 服务名
     * @return 服务下的子节点
     */
    public static List<String> getChildrenNode(CuratorFramework client, String serviceName) {
        // 若已缓存 则直接返回
        if (SERVICE_ADDRESS_MAP.containsKey(serviceName)) {
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }
        List<String> result = null;
        // 服务节点路径
        String servicePath = ZK_ROOT_PATH + "/" + serviceName;

        try {
            result = client.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, result);
            // 并为该服务接口的 子节点注册监听器 监听节点列表变化
            registerListen(serviceName, client);
        } catch (Exception e) {
            log.error("获取路径: [{}] 下的子节点列表失败!", servicePath);
        }
        return result;
    }

    /**
     * 注册服务节点变化监听器
     * @param serviceName 服务节点
     * @param client 客户端
     */
    private static void registerListen(String serviceName, CuratorFramework client) throws Exception {
        // 确定服务节点路径
        String servicePath = ZK_ROOT_PATH + "/" + serviceName;
        // 路径节点缓存
        PathChildrenCache cache = new PathChildrenCache(client, servicePath, true);
        // 注册监听器
        PathChildrenCacheListener listener = (curatorFramework, event) -> {
            // 服务提供者 节点列表
            List<String> serviceAddressList = curatorFramework.getChildren().forPath(servicePath);
            // 缓存
            SERVICE_ADDRESS_MAP.put(serviceName, serviceAddressList);
        };
        cache.getListenable().addListener(listener);
        cache.start();
    }

    /**
     * 清除服务节点信息
     * @param client zk 客户端
     * @param address 服务提供者地址
     */
    public static void clearRegistry(CuratorFramework client, InetSocketAddress address) {
        REGISTER_SERVICE_PATH.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(address.toString())) {
                    client.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("清除节点: [{}] 信息错误!!!", p);
            }
        });
        log.info("注册服务节点信息已清除, [{}]", REGISTER_SERVICE_PATH);
    }




}
