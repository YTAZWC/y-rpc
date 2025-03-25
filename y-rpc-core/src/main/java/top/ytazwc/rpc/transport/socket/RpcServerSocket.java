package top.ytazwc.rpc.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.ytazwc.rpc.config.RpcConfig;
import top.ytazwc.rpc.config.RpcServiceConfig;
import top.ytazwc.rpc.registry.provider.ServiceProvider;
import top.ytazwc.rpc.registry.provider.impl.ZkServiceProviderImpl;
import top.ytazwc.rpc.utils.ShutDownHookUtil;
import top.ytazwc.rpc.utils.concurrent.ThreadPoolFactory;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author 花木凋零成兰
 * @title RpcServerSocket
 * @date 2025-03-18 22:24
 * @package top.ytazwc.rpc.transport.socket
 * @description 基于socket实现的rpc服务端
 * <p color="blue">收到rpc客户端的连接请求 根据请求信息区调用并执行对应的方法 并将执行结果响应</p>
 */
@Slf4j
@Component
public class RpcServerSocket {

    // 线程池
    private final ExecutorService threadPool;
    // 服务提供者
    private final ServiceProvider provider;

    public RpcServerSocket() {
        threadPool = ThreadPoolFactory.getDefaultThreadPoolIfAbsent("socket-service-rpc-pool");
        provider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 服务提供者 进行服务注册
     * @param config 服务提供者配置信息
     */
    public void registerService(RpcServiceConfig config) {
        provider.publishService(config);
    }

    /**
     * 服务端处理客户端请求
     */
    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            // 获取服务地址
            String host = InetAddress.getLocalHost().getHostAddress();
            // 绑定服务地址 ip:port
            server.bind(new InetSocketAddress(host, RpcConfig.getPort()));
            // 注册暂停服务钩子 清除服务信息
            ShutDownHookUtil.getCustomShutdownHook().clearAll();

            Socket socket;
            // 等待客户端连接
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new RpcRequestSocketHandler(socket));
            }
            // 关闭连接池
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
        }
    }


}
