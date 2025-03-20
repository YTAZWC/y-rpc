package top.ytazwc.rpc.transport.socket;

import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.entity.RpcResponse;
import top.ytazwc.rpc.transport.handler.RpcRequestHandler;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author 00103943
 * @date 2025-03-20 14:56
 * @package top.ytazwc.rpc.transport.socket
 * @Description 基于Socket 的 rpc 请求处理器
 */
@Slf4j
public class RpcRequestSocketHandler implements Runnable {

    private final Socket socket;
    private final RpcRequestHandler handler;

    public RpcRequestSocketHandler(Socket socket) {
        this.socket = socket;
        this.handler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        log.info("处理客户端消息, 当前处理线程为: [{}]", Thread.currentThread().getName());

        try (
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ) {
            // 收到请求
            RpcRequest request = (RpcRequest) inputStream.readObject();
            // 处理请求并获取结果
            Object result = handler.handle(request);
            // 响应
            outputStream.writeObject(RpcResponse.success(result, request.getRequestId()));
            outputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("客户端处理消息出错! 错误信息:[{}]", e.getMessage());
        }

    }
}
