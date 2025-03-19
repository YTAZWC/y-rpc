package top.ytazwc.rpc.utils.concurrent;

import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author 00103943
 * @date 2025-03-19 08:47
 * @package top.ytazwc.rpc.utils.concurrent
 * @Description 自定义线程池配置 参数
 */
@Data
public class CustomThreadPoolConfig {

    // 默认参数
    // 核心线程池数
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    // 最大线程数
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 100;
    // 空闲线程存活时间
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;
    // 空闲线程存活时间单位
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    // 默认线程队列容量
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;

    // 提供配置参数
    // 核心线程数
    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    // 最大线程数
    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;
    // 存活时间
    private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    // 时间单位
    private TimeUnit unit = DEFAULT_TIME_UNIT;

    // 默认使用有界队列
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(DEFAULT_BLOCKING_QUEUE_CAPACITY);


}
