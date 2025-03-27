package top.ytazwc.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.registry.ServiceDiscovery;
import top.ytazwc.rpc.registry.zk.ZkServiceDiscoveryImpl;
import top.ytazwc.rpc.transport.RpcTransport;
import top.ytazwc.rpc.transport.netty.codec.RpcMessageDecoder;
import top.ytazwc.rpc.transport.netty.codec.RpcMessageEncoder;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author 花木凋零成兰
 * @title RpcClientNetty
 * @date 2025-03-26 20:05
 * @package top.ytazwc.rpc.transport.netty
 * @description 基于 Netty 实现的 rpc 客户端
 */
@Slf4j
@Component
public final class RpcClientNetty implements RpcTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public RpcClientNetty() {
        // 初始化资源
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接超时时间 5000 毫秒
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        // 15 s 没有向服务器发送任何市局 则发送心跳请求
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new RpcClientNettyHandler());
                    }
                });
        this.serviceDiscovery = SingletonFactory.getInstance(ZkServiceDiscoveryImpl.class);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest request) {
        return null;
    }

    // 获取远程地址的连接通道
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
//            channel = doConnect(inetSocketAddress); TODO
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

}
