package top.ytazwc.rpc.constant;

/**
 * @author 00103943
 * @date 2025-03-19 14:24
 * @package top.ytazwc.rpc.constant
 * @Description
 */
public interface RpcConstant {

    // 服务 默认监听端口
    int PORT = 9988;

    // 配置文件
    String RPC_PROPERTIES = "rpc.properties";

    // 自定义协议-魔数 占4个字节
    byte[] MAGIC_NUMBER = {
            (byte) 'y',
            (byte) 'r',
            (byte) 'p',
            (byte) 'c'
    };
    // 自定义协议 版本
    byte VERSION = 1;

    // 自定义协议头长度
    int HEAD_LENGTH = 16;

    // 心跳请求消息类型
    byte HEARTBEAT_REQUEST_TYPE = 1;
    // 心跳请求 默认传递数据为 ping
    String HEARTBEAT_REQUEST_DATA = "ping";

    // 心跳响应消息类型
    byte HEARTBEAT_RESPONSE_TYPE = 2;
    // 心跳响应 默认响应数据为 pong
    String HEARTBEAT_RESPONSE_DATA = "pong";

    // 请求消息类型
    byte REQUEST_TYPE = 3;

    // 响应消息类型
    byte RESPONSE_TYPE = 4;

    // 接收的最大帧长
    int MAX_FRAME_LENGTH = 8 * 1024 * 1024;



}
