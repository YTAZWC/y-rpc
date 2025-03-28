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
                    // TCP 默认开启 Nagle 算法，该算法作用是尽可能的发送大数据块，减少网络传输；TCP_NODELAY控制是否启用 Nagle 算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 表示系统用于临时存放已完成三次握手请求的队列的最大长度， 如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 客户端第一次进行请求的时候才会进行初始化
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
            log.error("启动服务时发生错误: {}", String.valueOf(e));
        } finally {
            // 关闭资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }

    }

}
