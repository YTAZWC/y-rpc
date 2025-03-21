package top.ytazwc.rpc.registry.loadbalance;

import top.ytazwc.rpc.entity.RpcRequest;

import java.util.List;

/**
 * @author 00103943
 * @date 2025-03-21 11:43
 * @package top.ytazwc.rpc.loadbalance
 * @Description 负载均衡接口
 */
public interface LoadBalance {

    /**
     * 根据服务请求 通过负载均衡调用服务
     * @param serviceAddressList 可选服务节点列表
     * @param request 请求信息
     * @return 对应服务地址
     */
    String selectService(List<String> serviceAddressList, RpcRequest request);

}
