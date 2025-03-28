package top.ytazwc.rpc.transport.netty.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.entity.RpcMessage;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.entity.RpcResponse;
import top.ytazwc.rpc.enums.CompressType;
import top.ytazwc.rpc.enums.SerializerType;
import top.ytazwc.rpc.transport.handler.RpcRequestHandler;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import static top.ytazwc.rpc.constant.RpcConstant.*;

/**
 * @author 00103943
 * @date 2025-03-28 11:45
 * @package top.ytazwc.rpc.transport.netty.server
 * @description 自定义服务端 ChannelHandler 处理客户端发送的数据
 */
@Slf4j
public class RpcServerNettyHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler handler;

    public RpcServerNettyHandler() {
        this.handler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("服务端接收到消息: [{}]", msg);
                // 消息类型
                RpcMessage receiveMsg = (RpcMessage) msg;
                byte messageType = receiveMsg.getMessageType();
                // 构造返回结果
                RpcMessage message = RpcMessage.builder()
                        .codec(SerializerType.KRYO.getCode())
                        .compressType(CompressType.GZIP.getCode())
                        .build();
                if (messageType == HEARTBEAT_REQUEST_TYPE) {
                    message.setMessageType(HEARTBEAT_RESPONSE_TYPE);
                    message.setData(HEARTBEAT_RESPONSE_DATA);
                } else {
                    RpcRequest request = (RpcRequest) receiveMsg.getData();
                    // 调用目标方法 并返回结果
                    Object result = handler.handle(request);
                    log.info("服务调用结果: [{}]", result.toString());
                    message.setMessageType(RESPONSE_TYPE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> response = RpcResponse.success(result, request.getRequestId());
                        message.setData(response);
                    } else {
                        RpcResponse<Object> response = RpcResponse.fail(request.getRequestId());
                        message.setData(response);
                        log.error("写入信息失败!");
                    }
                }
                ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("读超时,没有需要读的数据, 因此关闭连接!");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务端处理消息异常!");
        cause.printStackTrace();
        ctx.close();
    }

}
