package top.ytazwc.rpc.utils.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 00103943
 * @date 2025-03-19 09:39
 * @package top.ytazwc.rpc.utils.concurrent
 * @Description 线程池工具类
 */
@Slf4j
public class ThreadPoolUtil {

    /**
     * 打印线程池状态
     * @param pool 需要打印状态的线程池对象实例
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor pool) {

        if (pool.isShutdown() || pool.isTerminated()) {
            log.warn("=====================ThreadPool Status======================");
            log.warn("线程池已关闭!!!");
            log.warn("=================================================================");
        } else{
            ScheduledExecutorService scheduled = new ScheduledThreadPoolExecutor(
                    1, ThreadPoolFactory.createThreadFactory("print-thread-pool-status", false)
            );
            scheduled.scheduleAtFixedRate(() -> {
                log.info("=====================ThreadPool Status======================");
                log.info("ThreadPool Size: [{}]", pool.getPoolSize());
                log.info("Active Threads: [{}]", pool.getActiveCount());
                log.info("Number of Tasks : [{}]", pool.getCompletedTaskCount());
                log.info("Number of Tasks in Queue: {}", pool.getQueue().size());
                log.info("=================================================================");
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

}
