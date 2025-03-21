package top.ytazwc.rpc.registry.loadbalance;

import cn.hutool.core.collection.CollectionUtil;
import top.ytazwc.rpc.entity.RpcRequest;

import java.util.List;

/**
 * @author 00103943
 * @date 2025-03-21 11:45
 * @package top.ytazwc.rpc.registry.loadbalance
 * @Description 负载均衡抽象类 进一步扩展
 */
public abstract class LoadBalanceAbstract implements LoadBalance {

    @Override
    public String selectService(List<String> serviceAddressList, RpcRequest request) {
        if (CollectionUtil.isEmpty(serviceAddressList)) {
            return null;
        }
        // 服务提供者 只有一个节点 直接返回
        if (serviceAddressList.size() == 1) {
            return serviceAddressList.get(0);
        }
        return doSelect(serviceAddressList, request);
    }

    /**
     * 进一步封装 当服务节点 有两个及以上时 根据负载均衡策略进行选择
     * @param addressList 服务节点
     * @param request rpc请求信息
     * @return 返回具体服务地址
     */
    protected abstract String doSelect(List<String> addressList, RpcRequest request);

}
