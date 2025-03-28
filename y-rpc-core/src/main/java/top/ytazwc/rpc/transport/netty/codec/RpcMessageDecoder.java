package top.ytazwc.rpc.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.compress.Compress;
import top.ytazwc.rpc.compress.gzip.GzipCompress;
import top.ytazwc.rpc.entity.RpcMessage;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.entity.RpcResponse;
import top.ytazwc.rpc.enums.CompressType;
import top.ytazwc.rpc.enums.SerializerType;
import top.ytazwc.rpc.exception.MessageCoderException;
import top.ytazwc.rpc.serializer.Serializer;
import top.ytazwc.rpc.serializer.kryo.KryoSerializer;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.util.Arrays;

import static top.ytazwc.rpc.constant.RpcConstant.*;

/**
 * @author 00103943
 * @date 2025-03-27 15:02
 * @package top.ytazwc.rpc.transport.netty.codec
 * @description 自定义解码器
 * <br/> {@link LengthFieldBasedFrameDecoder} 是一个基于长度的解码器，用于解决 TCP 解包和粘卡问题
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * 构造器
     * @param maxFrameLength 最大帧长；决定可以接收的最大数据昌都，如果超过该长度 则数据会被丢弃；
     * @param lengthFieldOffset 长度字段偏移量，lengthFieldOffset 字段是跳过指定字节长度的字段
     * @param lengthFieldLength 长度字段的字节数
     * @param lengthAdjustment 要添加到 length 字段值的补偿值
     * @param initialBytesToStrip 跳过的字节数，如果需要接收所有的 header+body 数据，则值为0；如果只需要接收body数据，则值为跳过header的字节数
     */
    public RpcMessageDecoder(
            int maxFrameLength,
            int lengthFieldOffset,
            int lengthFieldLength,
            int lengthAdjustment,
            int initialBytesToStrip
    ) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public RpcMessageDecoder() {
        /*
         * lengthFieldOffset: 魔数占4个字节，版本占1个字节，因此这里跳过 5 个字节；
         * lengthFieldLength：长度字段占 4 个字节，所以这里为 4；
         * lengthAdjustment：读取数据偏移量，4个字节魔数 + 1个字节版本 + 4个字节长度，因此这里从左边第9个字节开始 fullLength-9
         * initialBytesToStrip：需要读取 header+body 所以是 0
         */
        this(MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= HEAD_LENGTH) {
                // 读取到的字节数 比 header 部分 大
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("解码失败!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {

        // 按顺序读取

        // 检查魔数
        checkMagicNumber(in);
        // 检查版本
        checkVersion(in);
        // 读取数据长度 占四个字节 int类型
        int fullLength = in.readInt();
        // 构建rpcMessage
        byte messageType = in.readByte();
        byte codec = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage message = RpcMessage.builder()
                .messageType(messageType)
                .codec(codec)
                .compressType(compressType)
                .requestId(requestId)
                .build();

        // 判断消息是否为心跳
        if (messageType == HEARTBEAT_REQUEST_TYPE) {
            message.setData(HEARTBEAT_REQUEST_DATA);
            return message;
        }
        if (messageType == HEARTBEAT_RESPONSE_TYPE) {
            message.setData(HEARTBEAT_RESPONSE_DATA);
            return message;
        }

        // 消息不为心跳 则计算消息体
        int bodyLength = fullLength - HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            String compressName = CompressType.getName(compressType);
            // 目前只有 gzip 压缩方式
            Compress compress = SingletonFactory.getInstance(GzipCompress.class);
            bs = compress.decompress(bs);
            // 反序列化 目前只采用 kryo
            String codecName = SerializerType.getName(codec);
            Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
            if (messageType == REQUEST_TYPE) {
                RpcRequest request = serializer.deserialize(bs, RpcRequest.class);
                message.setData(request);
            } else {
                RpcResponse response = serializer.deserialize(bs, RpcResponse.class);
                message.setData(response);
            }
        }

        return message;
    }

    // 读取版本 并检查
    private void checkVersion(ByteBuf in) {
        // 版本只占用 1 个字节；因此读一个字节
        byte version = in.readByte();
        if (version != VERSION) {
            throw new MessageCoderException("解码数据版本不对!" + version);
        }
    }

    // 检查魔数 判断是否是正确的数据包
    private void checkMagicNumber(ByteBuf in) {
        // 魔数占 4 个字节 读取四个字节
        int len = MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);

        // 检验是否正确
        for (int i = 0; i < len; ++ i) {
            if (tmp[i] != MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("不知道的数据包: " + Arrays.toString(tmp));
            }
        }

    }

}
