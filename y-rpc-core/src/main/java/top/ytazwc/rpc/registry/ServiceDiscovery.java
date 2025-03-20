package top.ytazwc.rpc.registry;

import top.ytazwc.rpc.entity.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author 00103943
 * @date 2025-03-18 16:42
 * @package top.ytazwc.rpc.registry
 * @Description 服务注册信息获取接口
 */
public interface ServiceDiscovery {

    /**
     * 根据服务请求获取调用服务
     * @param request rpc请求
     * @return 返回对应服务
     */
    InetSocketAddress lookupService(RpcRequest request);

}
