package top.ytazwc.rpc.service.impl;

import lombok.extern.slf4j.Slf4j;
import top.ytazwc.rpc.service.TestService;

/**
 * @author 00103943
 * @date 2025-03-21 14:35
 * @package top.ytazwc.rpc.service.impl
 * @Description
 */
@Slf4j
public class TestServiceImpl implements TestService {
    @Override
    public String testHello() {
        log.info("服务被调用!!!");
        return "Hello y Rpc !!!";
    }
}
