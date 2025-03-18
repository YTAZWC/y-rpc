package top.ytazwc.rpc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author 00103943
 * @date 2025-03-18 16:37
 * @package top.ytazwc.rpc
 * @Description
 */
@Slf4j
public class LogTests {

    @Test
    public void test() {
        log.error("测试日志:{}", "是的");
    }

}
