package top.ytazwc.rpc;

import org.junit.Test;
import top.ytazwc.rpc.utils.CuratorUtils;

import java.net.InetSocketAddress;

/**
 * @author 花木凋零成兰
 * @title InetSocketAddressTests
 * @date 2025-03-20 22:15
 * @package top.ytazwc.rpc
 * @description
 */
public class InetSocketAddressTests {

    @Test
    public void testServicePath() {
        String serviceName = "top.ytazwc.impl.lll";
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8899);
        // address.toString 结果为 /127.0.0.1:8899
        String servicePath = CuratorUtils.ZK_ROOT_PATH + "/" + serviceName + address.toString();
        System.out.println(servicePath);
    }

}
