package top.ytazwc.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.ytazwc.rpc.config.RpcConfig;
import top.ytazwc.rpc.config.RpcServiceConfig;
import top.ytazwc.rpc.registry.provider.ServiceProvider;
import top.ytazwc.rpc.registry.provider.impl.ZkServiceProviderImpl;
import top.ytazwc.rpc.transport.netty.codec.RpcMessageDecoder;
import top.ytazwc.rpc.transport.netty.codec.RpcMessageEncoder;
import top.ytazwc.rpc.utils.RuntimeUtil;
import top.ytazwc.rpc.utils.ShutDownHookUtil;
import top.ytazwc.rpc.utils.concurrent.ThreadPoolFactory;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author 花木凋零成兰
 * @title RpcServerNetty
 * @date 2025-03-26 22:35
 * @package top.ytazwc.rpc.transport.netty.client
 * @description 基于 Netty 实现 服务端；用于接收客户端消息 以及服务注册
 */
@Slf4j
@Component
public class RpcServerNetty {

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start() {
        ShutDownHookUtil.getCustomShutdownHook().clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactory.createThreadFactory("service-handler-group", false)
        );

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 30 秒之内没有收到客户端请求 就关闭连接
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, new RpcServerNettyHandler());
                        }
                    });
            // 绑定端口 同步等待绑定成功
            ChannelFuture f = b.bind(host, RpcConfig.getPort()).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务时发生错误: " + e);
        } finally {
            // 关闭资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }

    }

}
