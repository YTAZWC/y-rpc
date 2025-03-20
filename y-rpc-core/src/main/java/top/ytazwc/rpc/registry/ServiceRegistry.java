package top.ytazwc.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author 00103943
 * @date 2025-03-20 15:29
 * @package top.ytazwc.rpc.registry
 * @Description 服务注册接口
 */
public interface ServiceRegistry {

    /**
     * 注册服务到服务注册中心
     * @param serviceName 服务唯一标识，服务名称
     * @param address 服务提供者地址
     */
    void registryService(String serviceName, InetSocketAddress address);

}
