package top.ytazwc.rpc.registry.zk;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.enums.RpcErrorMessage;
import top.ytazwc.rpc.exception.RpcException;
import top.ytazwc.rpc.registry.ServiceDiscovery;
import top.ytazwc.rpc.registry.loadbalance.LoadBalance;
import top.ytazwc.rpc.registry.loadbalance.impl.RandomLoadBalance;
import top.ytazwc.rpc.utils.CuratorUtils;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author 00103943
 * @date 2025-03-21 13:44
 * @package top.ytazwc.rpc.registry.zk
 * @Description 基于 Zookeeper 实现的服务发现接口
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    // 负载均衡算法
    // 通过负载均衡来选择提供服务的节点 提高可用性
    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = SingletonFactory.getInstance(RandomLoadBalance.class);
    }

    /**
     * 根据rpc请求 获取对应服务的地址
     * @param request rpc请求
     * @return 调用服务节点地址
     */
    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        // 获取服务唯一标识
        String serviceName = request.getRpcServiceName();
        // Zookeeper 客户端
        CuratorFramework client = CuratorUtils.getZkClient();
        // 获取服务下的 服务节点列表
        List<String> serviceNodeList = CuratorUtils.getChildrenNode(client, serviceName);

        if (CollectionUtil.isEmpty(serviceNodeList)) {
            // 服务列表为空
            throw new RpcException(RpcErrorMessage.SERVICE_NOT_FOUND, serviceName);
        }

        // 调用负载均衡算法
        String serviceAddress = loadBalance.selectService(serviceNodeList, request);
        log.info("成功找到服务, 服务 [{}] 地址为: [{}]", serviceName, serviceAddress);

        // 分割地址 得到 ip:port
        String[] ipAndPort = serviceAddress.split(":");
        String host = ipAndPort[0];
        int port = Integer.parseInt(ipAndPort[1]);

        // 封装并返回
        return new InetSocketAddress(host, port);
    }
}
