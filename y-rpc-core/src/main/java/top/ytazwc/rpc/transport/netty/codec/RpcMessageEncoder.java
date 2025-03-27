package top.ytazwc.rpc.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.compress.Compress;
import top.ytazwc.rpc.entity.RpcMessage;
import top.ytazwc.rpc.enums.SerializerType;
import top.ytazwc.rpc.exception.MessageCoderException;
import top.ytazwc.rpc.serializer.Serializer;
import top.ytazwc.rpc.serializer.kryo.KryoSerializer;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static top.ytazwc.rpc.constant.RpcConstant.*;

/**
 * @author 00103943
 * @date 2025-03-27 09:05
 * @package top.ytazwc.rpc.transport.netty.codec
 * @description 自定义消息编码器
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    // 编码方法
    @Override
    protected void encode(ChannelHandlerContext context, RpcMessage message, ByteBuf buf) throws Exception {
        try {
            // 头部写入魔数 4 字节
            buf.writeBytes(MAGIC_NUMBER);
            // 写入版本 1 字节
            buf.writeByte(VERSION);
            // 头部留出 4 个字节 用于写入 传输数据长度
            buf.writerIndex(buf.writerIndex() + 4);
            // 获取消息类型 1 个字节
            byte messageType = message.getMessageType();
            buf.writeByte(messageType);
            // 写入序列化类型 1 个字节
            byte codec = message.getCodec();
            buf.writeByte(codec);
            // 写入压缩类型 1 个字节
            buf.writeByte(message.getCompressType());
            // 4 个字节 请求序号
            buf.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // 数据数组
            byte[] bodyBytes = null;
            // 请求长度 包含请求头的大小，因此最小固定为 16 个字节
            int fullLength = HEAD_LENGTH;

            // 根据消息类型 来确定rpc请求消息长度
            if (messageType != HEARTBEAT_REQUEST_TYPE && messageType != HEARTBEAT_RESPONSE_TYPE) {
                // 如果不是心跳rpc请求；则计算完整请求长度
                // 目前序列化方式 只采用 Kryo
                String codecName = SerializerType.getName(codec);
                Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
                bodyBytes = serializer.serialize(message.getData());
                // 压缩方式 目前只采用 gzip
                Compress compress = SingletonFactory.getInstance(Compress.class);
                bodyBytes = compress.compress(bodyBytes);
                // 得到总长度
                fullLength += bodyBytes.length;
            }

            if (Objects.nonNull(bodyBytes)) {
                // 写入具体传输数据
                buf.writeBytes(bodyBytes);
            }
            // 当前写入位置
            int writeIndex = buf.writerIndex();
            // 移动到写入数据长度位置
            buf.writerIndex(writeIndex - fullLength + MAGIC_NUMBER.length + 1);
            // 写入数据长度
            buf.writeInt(fullLength);
            // 再移动到末尾位置
            buf.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("编码失败!", e);
            throw new MessageCoderException("编码失败!", e);
        }
    }
}
