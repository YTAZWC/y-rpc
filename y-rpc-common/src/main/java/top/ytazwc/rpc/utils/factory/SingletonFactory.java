package top.ytazwc.rpc.utils.factory;

import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 00103943
 * @date 2025-03-18 16:54
 * @package top.ytazwc.rpc.utils.factory
 * @Description 单例工厂：对各个接口的实现类实例进行统一管理
 */
@NoArgsConstructor
public final class SingletonFactory {

    // 存放接口及对应接口实例 接口名-接口实例
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    // 锁 保证初次创建实例时线程安全
    private static final Object LOCK = new Object();

    public static <T> T getInstance(Class<T> tClass) {
        if (tClass == null) {
            throw new IllegalArgumentException();
        }
        // 类名
        String key = tClass.toString();
        if (OBJECT_MAP.containsKey(key)) {
            // 若实例已经创建 则获取对应实例并转化为对应的类型返回
            return tClass.cast(OBJECT_MAP.get(key));
        } else {
            // 当是初次创建实例时 注意并发问题
            synchronized (LOCK) {
                // 只有一个线程步入
                if (!OBJECT_MAP.containsKey(key)) {
                    try {
                        // 创建实例
                        T instance = tClass.getDeclaredConstructor().newInstance();
                        OBJECT_MAP.put(key, instance);
                        return instance;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // 当第一个线程创建好实例后
                    // 后续等待线程则直接获取实例即可
                    return tClass.cast(OBJECT_MAP.get(key));
                }
            }
        }

    }

}
