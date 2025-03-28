package top.ytazwc.rpc.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.ytazwc.rpc.annotations.RpcReference;
import top.ytazwc.rpc.annotations.RpcService;
import top.ytazwc.rpc.config.RpcServiceConfig;
import top.ytazwc.rpc.exception.RpcException;
import top.ytazwc.rpc.registry.provider.ServiceProvider;
import top.ytazwc.rpc.registry.provider.impl.ZkServiceProviderImpl;
import top.ytazwc.rpc.transport.RpcTransport;
import top.ytazwc.rpc.transport.netty.client.RpcClientNetty;
import top.ytazwc.rpc.transport.proxy.RpcClientProxy;
import top.ytazwc.rpc.utils.factory.SingletonFactory;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author 00103943
 * @date 2025-03-25 10:41
 * @package top.ytazwc.rpc.spring
 * @description 用于实现服务注册 并实现服务消费bean
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    // 服务提供者 进行服务注册
    private final ServiceProvider serviceProvider;
    // 服务消费者 属性赋值
    private final RpcTransport transport;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.transport = SingletonFactory.getInstance(RpcClientNetty.class);
    }

    /*
     * 判断类上是否有 RpcService 注解：
     * 有则取出 group 和 version 值并进行服务发布
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            // 有服务注册注解 进行服务注册
            log.info("[{}] 有注解: [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // 获取注解
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // 进行注册配置
            RpcServiceConfig config = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean)
                    .build();
            serviceProvider.publishService(config);
        }
        return bean;
    }

    /*
     * 判断类的属性上是否有 RpcReference 注解：
     * 有则说明是消费服务；通过反射进行属性赋值
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        // 获得属性字段
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference reference = declaredField.getAnnotation(RpcReference.class);
            if (Objects.nonNull(reference)) {
                RpcServiceConfig config = RpcServiceConfig.builder()
                        .group(reference.group())
                        .version(reference.version()).build();
                RpcClientProxy proxy = new RpcClientProxy(transport, config);
                Object client = proxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);

                try {
                    declaredField.set(bean, client);
                } catch (IllegalAccessException e) {
                    log.error("客户端 bean 注入失败!!!", e);
                    throw new RpcException("客户端 bean 注入失败!!!", e);
                }
            }
        }
        return bean;
    }

}
