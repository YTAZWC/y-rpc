package top.ytazwc.rpc.registry.zk;

import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.registry.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * @author 00103943
 * @date 2025-03-20 15:55
 * @package top.ytazwc.rpc.registry.zk
 * @Description zookeeper 服务注册中心 实现服务注册
 */
@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registryService(String serviceName, InetSocketAddress address) {

    }
}
