package top.ytazwc.rpc.registry.provider.impl;

import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.config.RpcConfig;
import top.ytazwc.rpc.config.RpcServiceConfig;
import top.ytazwc.rpc.enums.RpcErrorMessage;
import top.ytazwc.rpc.exception.RpcException;
import top.ytazwc.rpc.registry.ServiceRegistry;
import top.ytazwc.rpc.registry.provider.ServiceProvider;
import top.ytazwc.rpc.registry.zk.ZkServiceRegistryImpl;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 00103943
 * @date 2025-03-19 14:11
 * @package top.ytazwc.rpc.registry.provider
 * @Description 服务提供者 调用进行服务注册接口实现 基于Zookeeper
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    // 缓存 service 服务实现类 interfaceName + version + group ： obj
    private final Map<String, Object> serviceMap;

    // 注册服务集合 避免重复
    private final Set<String> registeredService;

    // 服务注册接口 调用该接口进行服务注册
    private final ServiceRegistry registry;

    public ZkServiceProviderImpl() {

        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        registry = SingletonFactory.getInstance(ZkServiceRegistryImpl.class);

    }

    @Override
    public void addService(RpcServiceConfig config) {
        // 获取服务名
        String serviceName = config.getRpcServiceName();
        // 服务已经注册过 则不重复增加
        if (registeredService.contains(serviceName)) {
            return ;
        }
        registeredService.add(serviceName);
        // 缓存实例
        serviceMap.put(serviceName, config.getService());
        log.info("新注册服务: [{}] 接口为: [{}]", serviceName, config.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (Objects.isNull(service)) {
            throw new RpcException(RpcErrorMessage.SERVICE_NOT_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(config);
            registry.registryService(config.getRpcServiceName(), new InetSocketAddress(host, RpcConfig.getPort()));
        } catch (UnknownHostException e) {
            log.error("获取本机域名失败!!!", e);
        }
    }
}
