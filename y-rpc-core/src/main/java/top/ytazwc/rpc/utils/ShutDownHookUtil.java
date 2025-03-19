package top.ytazwc.rpc.utils;

import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.utils.concurrent.ThreadPoolFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static top.ytazwc.rpc.constant.RpcConstant.PORT;

/**
 * @author 00103943
 * @date 2025-03-19 14:26
 * @package top.ytazwc.rpc.utils
 * @Description 关闭客户端与服务端连接时执行部分操作
 */
@Slf4j
public class ShutDownHookUtil {

    private static final ShutDownHookUtil SHUTDOWN_HOOK = new ShutDownHookUtil();

    public static ShutDownHookUtil getCustomShutdownHook() {
        return SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 获取服务地址
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
                // 清除服务注册信息 TODO
//                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
            ThreadPoolFactory.shutDownAllThreadPool();
        }));
    }

}
