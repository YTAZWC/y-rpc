package top.ytazwc.rpc.transport.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.entity.RpcMessage;
import top.ytazwc.rpc.entity.RpcResponse;
import top.ytazwc.rpc.enums.CompressType;
import top.ytazwc.rpc.enums.SerializerType;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.net.InetSocketAddress;

import static top.ytazwc.rpc.constant.RpcConstant.*;

/**
 * @author 花木凋零成兰
 * @title RpcClientNettyHandler
 * @date 2025-03-27 21:30
 * @package top.ytazwc.rpc.transport.netty.client
 * @description 自定义客户端请求处理器 处理服务端发送的数据
 * <br>
 * 通过继承ChannelInboundHandlerAdapter而继承ChannelInboundHandler；内部ChannelRead方法会自动释放 ByteBuf 避免内存泄露
 */
@Slf4j
public class RpcClientNettyHandler extends ChannelInboundHandlerAdapter {

    // 未处理的请求集合
    private final UnprocessedRequests unprocessedRequests;
    // 基于netty的客户端
    private final RpcClientNetty clientNetty;

    public RpcClientNettyHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.clientNetty = SingletonFactory.getInstance(RpcClientNetty.class);
    }

    // 读请求并发送给服务端
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("客户端收到消息: [{}]", msg);

            if (msg instanceof RpcMessage) {
                // 收到正确的消息
                RpcMessage message = (RpcMessage) msg;
                // 获取消息类型
                byte messageType = message.getMessageType();
                if (messageType == HEARTBEAT_RESPONSE_TYPE) {
                    log.info("心跳请求: [{}]", message.getData());
                } else if (messageType == RESPONSE_TYPE) {
                    // 正常响应
                    RpcResponse<Object> response = (RpcResponse<Object>) message.getData();
                    unprocessedRequests.complete(response);
                }

            }

        } finally {
            // 释放消息引用 避免内存泄露
            ReferenceCountUtil.release(msg);
        }

    }

    // 处理 IdleStateEvent 事件类型
    // IdleStateEvent 检测通道是否空闲 事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // 若通道没有写操作
                log.info("进行消息写入, 消息写入事件发生在: [{}]", ctx.channel().remoteAddress());
                // 获取与远程地址对应的连接通道
                Channel channel = clientNetty.getChannel((InetSocketAddress)ctx.channel().remoteAddress());
                // 构建信息 发送心跳请求
                RpcMessage message = RpcMessage.builder()
                        .codec(SerializerType.KRYO.getCode())
                        .compressType(CompressType.GZIP.getCode())
                        .messageType(HEARTBEAT_REQUEST_TYPE)
                        .data(HEARTBEAT_REQUEST_DATA)
                        .build();
                // 将心跳消息写入通道 当写入消息失败时关闭通道的监听器
                channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    // 处理客户端消息发生异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端消息处理出现异常: ", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
