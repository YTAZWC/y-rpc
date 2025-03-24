package top.ytazwc.rpc.transport.handler;

import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.exception.RpcException;
import top.ytazwc.rpc.registry.provider.ServiceProvider;
import top.ytazwc.rpc.registry.provider.impl.ZkServiceProviderImpl;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 00103943
 * @date 2025-03-20 14:58
 * @package top.ytazwc.rpc.transport.handler
 * @Description rpc 请求处理器
 */
@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider provider;

    public RpcRequestHandler() {
        provider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 处理rpc请求执行
     * @param request rpc请求
     * @return 执行结果
     */
    public Object handle(RpcRequest request) {
        // 得到目标服务
        Object service = provider.getService(request.getRpcServiceName());
        // 执行并返回
        return invokeTargetMethod(request, service);
    }

    /**
     * 执行具体目标方法
     * @param request 请求
     * @param service 目标服务
     * @return 目标服务执行结果
     */
    private Object invokeTargetMethod(RpcRequest request, Object service) {
        Object result;
        try {
            // 通过反射获得被调用的方法
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParameters());
            log.info("服务: [{}] 调用方法: [{}] 成功!", request.getInterfaceName(), request.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }

}
