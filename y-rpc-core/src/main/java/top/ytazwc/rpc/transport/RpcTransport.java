package top.ytazwc.rpc.transport;

import top.ytazwc.rpc.entity.RpcRequest;

/**
 * @author 00103943
 * @date 2025-03-18 16:08
 * @package top.ytazwc.rpc.transport
 * @Description rpc 网络传输接口
 */
public interface RpcTransport {

    /**
     * 发送rpc请求方法
     * @param request 请求信息实例
     * @return 返回结果
     */
    Object sendRpcRequest(RpcRequest request);

}
