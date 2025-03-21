package top.ytazwc.rpc.transport.socket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.exception.RpcException;
import top.ytazwc.rpc.registry.ServiceDiscovery;
import top.ytazwc.rpc.registry.zk.ZkServiceDiscoveryImpl;
import top.ytazwc.rpc.transport.RpcTransport;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author 00103943
 * @date 2025-03-18 16:22
 * @package top.ytazwc.rpc.transport.socket
 * @Description 基于Socket发送rpc请求的客户端
 */
@Slf4j
@AllArgsConstructor
public class RpcClientSocket implements RpcTransport {

    private final ServiceDiscovery serviceDiscovery;

    public RpcClientSocket() {
        this.serviceDiscovery = SingletonFactory.getInstance(ZkServiceDiscoveryImpl.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest request) {
        // 构建rpc请求
        // 获取调用服务地址
        InetSocketAddress address = serviceDiscovery.lookupService(request);
        try (Socket socket = new Socket()) {
            // 与服务器建立连接
            socket.connect(address);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            // 输出流 发送请求数据
            outputStream.writeObject(request);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            // 输入流 读取响应数据
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败: " + e);
        }
    }
}
