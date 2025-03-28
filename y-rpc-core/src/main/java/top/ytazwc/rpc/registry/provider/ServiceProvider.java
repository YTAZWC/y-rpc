package top.ytazwc.rpc.registry.provider;

import top.ytazwc.rpc.config.RpcServiceConfig;

/**
 * @author 00103943
 * @date 2025-03-19 13:55
 * @package top.ytazwc.rpc.registry.provider
 * @Description 服务提供接口
 */
public interface ServiceProvider {

    /**
     * 新增服务 缓存
     * @param config 服务配置信息
     */
    void addService(RpcServiceConfig config);

    /**
     * 从缓存中获取服务实例
     * @param rpcServiceName 服务唯一标识
     * @return 返回服务实例
     */
    Object getService(String rpcServiceName);

    /**
     * 注册服务
     * @param config 服务配置信息
     */
    void publishService(RpcServiceConfig config);

}
