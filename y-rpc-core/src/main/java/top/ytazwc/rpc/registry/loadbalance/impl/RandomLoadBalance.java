package top.ytazwc.rpc.registry.loadbalance.impl;

import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.registry.loadbalance.LoadBalanceAbstract;

import java.util.List;
import java.util.Random;

/**
 * @author 00103943
 * @date 2025-03-21 11:55
 * @package top.ytazwc.rpc.registry.loadbalance.impl
 * @Description 采用随机算法 实现负载均衡
 *
 */
public class RandomLoadBalance extends LoadBalanceAbstract {

    @Override
    protected String doSelect(List<String> addressList, RpcRequest request) {
        // 节点数
        int size = addressList.size();
        // 自带的随机类
        Random random = new Random();
        // 随机返回节点列表中的节点地址
        return addressList.get(random.nextInt(size));
    }
}
