package top.ytazwc.rpc.entity;

import lombok.*;

/**
 * @author 花木凋零成兰
 * @title RpcMessage
 * @date 2025-03-26 20:41
 * @package top.ytazwc.rpc.entity
 * @description 自定义协议 rpc 传递消息类
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RpcMessage {

    // 消息类型 占一个字节
    private byte messageType;

    // 序列化类型 占一个字节
    private byte codec;

    // 压缩类型
    private byte compress;

    // 请求id 占四个字节
    private byte requestId;

    // 具体请求数据 一般占用 16 个字节
    private Object data;

}
