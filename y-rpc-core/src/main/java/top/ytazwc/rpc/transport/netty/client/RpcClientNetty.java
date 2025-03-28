package top.ytazwc.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.ytazwc.rpc.entity.RpcMessage;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.entity.RpcResponse;
import top.ytazwc.rpc.enums.CompressType;
import top.ytazwc.rpc.enums.SerializerType;
import top.ytazwc.rpc.registry.ServiceDiscovery;
import top.ytazwc.rpc.registry.zk.ZkServiceDiscoveryImpl;
import top.ytazwc.rpc.transport.RpcTransport;
import top.ytazwc.rpc.transport.netty.codec.RpcMessageDecoder;
import top.ytazwc.rpc.transport.netty.codec.RpcMessageEncoder;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static top.ytazwc.rpc.constant.RpcConstant.REQUEST_TYPE;

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

    // 连接服务器 返回通道 用户向客户端发送消息
    @SneakyThrows   // 简化对检查异常的处理
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接 [{}] 成功!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public Object sendRpcRequest(RpcRequest request) {
        // 返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 获取服务端地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request);
        // 获取服务连接通道
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            // 通道空闲
            unprocessedRequests.put(request.getRequestId(), resultFuture);
            RpcMessage message = RpcMessage.builder()
                    .data(request)
                    .codec(SerializerType.KRYO.getCode())
                    .compressType(CompressType.GZIP.getCode())
                    .messageType(REQUEST_TYPE)
                    .build();
            channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端成功发送消息: [{}]", message);
                } else {
                    future.channel().closeFuture();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("客户端发送消息失败!", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    // 获取远程地址的连接通道
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
