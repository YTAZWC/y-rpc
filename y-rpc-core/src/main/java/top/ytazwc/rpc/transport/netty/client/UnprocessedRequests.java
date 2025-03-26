package top.ytazwc.rpc.transport.netty.client;

import top.ytazwc.rpc.entity.RpcResponse;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 花木凋零成兰
 * @title UnprocessedRequests
 * @date 2025-03-26 20:11
 * @package top.ytazwc.rpc.transport.netty.client
 * @description 用于记录 服务器未处理的请求
 */
public class UnprocessedRequests {

    // 用于缓存 等待服务器响应的请求
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    /**
     * 得到响应 消除得到响应的请求
     * @param response 得到的响应
     */
    public void complete(RpcResponse<Object> response) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(response.getRequestId());
        if (Objects.isNull(future)) {
            throw new IllegalStateException();
        }

        future.complete(response);
    }


}
