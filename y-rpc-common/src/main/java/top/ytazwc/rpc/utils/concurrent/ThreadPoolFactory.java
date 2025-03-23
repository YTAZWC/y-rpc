package top.ytazwc.rpc.utils.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author 00103943
 * @date 2025-03-19 08:55
 * @package top.ytazwc.rpc.utils.concurrent
 * @Description 创建 线程池 工具类
 */
@Slf4j
@NoArgsConstructor
public final class ThreadPoolFactory {

    // 线程池单例容器
    // 通过线程名来区分不同线程池 线程池-线程池实例
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    /**
     * 获取默认配置线程池 若线程池不存在则创建新的线程池
     * @param threadNamePrefix 线程池名
     * @return 返回需要的线程池
     */
    public static ExecutorService getDefaultThreadPoolIfAbsent(String threadNamePrefix) {
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    /**
     * 获取自定义线程池 若线程池不存在则创建新的线程池
     * @param customThreadPoolConfig 自定义配置
     * @param threadNamePrefix 线程池标识
     * @return 所需线程池
     */
    public static ExecutorService getCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix) {
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    /**
     * 创建自定义线程池
     * @param config 线程池配置
     * @param namePrefix 线程名
     * @param daemon 线程池线程是否为守护线程
     * @return 自定义线程池
     */
    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig config, String namePrefix, Boolean daemon) {
        // 尝试创建线程池 存在则返回 不存在则创建新的
        ExecutorService pool = THREAD_POOLS.computeIfAbsent(namePrefix , k -> createThreadPool(config, namePrefix, daemon));
        // 线程池被消除 则重新创建
        if (pool.isShutdown() || pool.isTerminated()) {
            // 删除无用线程实例
            THREAD_POOLS.remove(namePrefix);
            pool = createThreadPool(config, namePrefix, daemon);
            THREAD_POOLS.put(namePrefix, pool);
        }
        return pool;
    }

    /**
     * 创建线程池
     * @param config 线程池参数
     * @param namePrefix 线程池名前缀
     * @param daemon    是否为守护线程
     * @return 创建线程池结果
     */
    private static ExecutorService createThreadPool(CustomThreadPoolConfig config, String namePrefix, Boolean daemon) {
        // 获取线程工厂
        ThreadFactory factory = createThreadFactory(namePrefix, daemon);
        // 创建并返回线程池
        return new ThreadPoolExecutor(
                config.getCorePoolSize(),
                config.getMaximumPoolSize(),
                config.getKeepAliveTime(),
                config.getUnit(),
                config.getWorkQueue(),
                factory
        );
    }

    /**
     * 创建线程工厂
     * @param namePrefix    线程名
     * @param daemon        是否为守护线程
     * @return 线程工厂
     */
    public static ThreadFactory createThreadFactory(String namePrefix, Boolean daemon) {
        if (Objects.nonNull(namePrefix)) {
            if (Objects.isNull(daemon)) {
                // 设置线程工厂：1、线程名称格式
                return new ThreadFactoryBuilder()
                        .setNameFormat(namePrefix + "-%d")
                        .build();
            } else {
                // 设置线程工厂：1、线程名称格式 2、是否为守护线程
                return new ThreadFactoryBuilder()
                        .setNameFormat(namePrefix + "-%d")
                        .setDaemon(daemon)
                        .build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    /**
     * 关闭所有线程池
     */
    public static void shutDownAllThreadPool() {
        log.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }

}
