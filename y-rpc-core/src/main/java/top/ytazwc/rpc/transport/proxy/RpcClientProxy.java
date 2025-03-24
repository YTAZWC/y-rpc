package top.ytazwc.rpc.transport.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.config.RpcServiceConfig;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.entity.RpcResponse;
import top.ytazwc.rpc.enums.RpcErrorMessage;
import top.ytazwc.rpc.enums.RpcResCode;
import top.ytazwc.rpc.exception.RpcException;
import top.ytazwc.rpc.transport.RpcTransport;
import top.ytazwc.rpc.transport.socket.RpcClientSocket;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;

/**
 * @author 00103943
 * @date 2025-03-24 09:14
 * @package top.ytazwc.rpc.transport
 * @Description 客户端动态代理；封装调用远程方法细节 使客户端调用远程服务如同调用本地服务一般
 */
@Slf4j
@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {

    // 用于发送rpc请求
    private final RpcTransport rpcTransport;
    // rpc服务配置
    private final RpcServiceConfig config;

    public RpcClientProxy(RpcTransport rpcTransport) {
        this.rpcTransport = rpcTransport;
        this.config = new RpcServiceConfig();
    }

    // 获取代理目标类实例
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    // 代理实际调用方法
    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("调用方法: [{}]", method.getName());

        // 构造请求
        RpcRequest request = RpcRequest.builder()
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(config.getGroup())
                .version(config.getVersion())
                .methodName(method.getName())
                .build();

        // 响应
        RpcResponse<Object> response = null;
        if (rpcTransport instanceof RpcClientSocket) {
            response = (RpcResponse<Object>) rpcTransport.sendRpcRequest(request);
        }

        // 对结果进行检查
        this.check(request, response);

        return response.getData();
    }

    /**
     * 检查 请求与响应是否符合
     * @param request 请求实例
     * @param response 响应实例
     */
    private void check(RpcRequest request, RpcResponse<Object> response) {
        String detail = "服务名: " + request.getInterfaceName();

        // 相应判空
        if (Objects.isNull(response)) {
            throw new RpcException(RpcErrorMessage.SERVER_INVOCATION_FAILURE, detail);
        }

        // 响应与请求是否对应
        if (!StrUtil.equals(request.getRequestId(), response.getRequestId())) {
            throw new RpcException(RpcErrorMessage.REQUEST_NOT_MATCH_RESPONSE, detail);
        }

        if (Objects.isNull(response.getCode()) || !response.getCode().equals(RpcResCode.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessage.SERVER_INVOCATION_FAILURE, detail);
        }

    }

}
