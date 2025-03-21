package top.ytazwc.rpc.registry.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import top.ytazwc.rpc.registry.ServiceRegistry;
import top.ytazwc.rpc.utils.CuratorUtils;

import java.net.InetSocketAddress;

import static top.ytazwc.rpc.utils.CuratorUtils.ZK_ROOT_PATH;

/**
 * @author 00103943
 * @date 2025-03-20 15:55
 * @package top.ytazwc.rpc.registry.zk
 * @Description zookeeper 服务注册中心 实现服务注册
 */
@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {

    /**
     * 服务注册
     * @param serviceName 服务唯一标识，服务名称
     * @param address 服务提供者地址
     */
    @Override
    public void registryService(String serviceName, InetSocketAddress address) {
        // 构建服务路径
        String servicePath = ZK_ROOT_PATH + "/" + serviceName + address.toString();
        // 获取客户端
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 注册服务
        CuratorUtils.addPersistentNode(zkClient, servicePath);
    }

}
