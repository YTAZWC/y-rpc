package top.ytazwc.rpc;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import top.ytazwc.rpc.config.RpcServiceConfig;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.registry.loadbalance.LoadBalance;
import top.ytazwc.rpc.registry.loadbalance.impl.RandomLoadBalance;
import top.ytazwc.rpc.service.TestService;
import top.ytazwc.rpc.service.impl.TestServiceImpl;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author 00103943
 * @date 2025-03-21 14:37
 * @package top.ytazwc.rpc
 * @Description 测试负载均衡
 */
@Slf4j
public class LoadBalanceTests {

    private final LoadBalance loadBalance = SingletonFactory.getInstance(RandomLoadBalance.class);

    @Test
    public void testRandom() {
        // 构建节点列表
        List<String> addressList = Arrays.asList("127.0.0.1:8989", "127.0.0.1:8787", "127.0.0.1:8686");

        // 服务
        TestService service = new TestServiceImpl();

        // 构建配置
        RpcServiceConfig config = RpcServiceConfig.builder()
                .service(service)
                .version("version")
                .group("group")
                .build();

        log.info("rpc service config: [{}]", config);

        // 构建请求
        RpcRequest request = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(config.getServiceName())
                .version(config.getVersion())
                .group(config.getGroup())
                .parameters(service.getClass().getTypeParameters())
                .build();

        log.info("rpc request: [{}]", request);

        for (int i = 0; i < 10; i++) {
            String serviceAddress = loadBalance.selectService(addressList, request);
            log.info("service address: [{}]", serviceAddress);
        }



    }

}
