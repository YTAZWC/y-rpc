package top.ytazwc.rpc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import top.ytazwc.rpc.config.RpcServiceConfig;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.registry.ServiceDiscovery;
import top.ytazwc.rpc.registry.ServiceRegistry;
import top.ytazwc.rpc.registry.zk.ZkServiceDiscoveryImpl;
import top.ytazwc.rpc.registry.zk.ZkServiceRegistryImpl;
import top.ytazwc.rpc.service.TestService;
import top.ytazwc.rpc.service.impl.TestServiceImpl;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author 00103943
 * @date 2025-03-21 14:38
 * @package top.ytazwc.rpc
 * @Description 服务注册测试
 */
@Slf4j
public class ServiceRegistryTests {

    // 服务注册
    private final ServiceRegistry registry = SingletonFactory.getInstance(ZkServiceRegistryImpl.class);

    // 服务发现
    private final ServiceDiscovery discovery = SingletonFactory.getInstance(ZkServiceDiscoveryImpl.class);

    @Test
    public void testRegistry() {
        // 构建服务地址
        InetSocketAddress serviceAddress = new InetSocketAddress("127.0.0.1", 9898);
        // 提供服务
        TestService service = new TestServiceImpl();

        // 构建 config
        RpcServiceConfig config = RpcServiceConfig.builder()
                .group("group")
                .version("version")
                .service(service)
                .build();

        registry.registryService(config.getRpcServiceName(), serviceAddress);

        // 构建rpc请求
        RpcRequest request = RpcRequest.builder()
                .group("group")
                .version("version")
                .interfaceName(config.getServiceName())
                .requestId(UUID.randomUUID().toString())
                .build();

        InetSocketAddress inetSocketAddress = discovery.lookupService(request);

        // 判断服务发现获得的地址与注册地址是否一致
        log.info("服务注册地址: [{}]", serviceAddress);
        log.info("服务发现地址: [{}]", inetSocketAddress);

    }

}
