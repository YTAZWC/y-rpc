package top.ytazwc.rpc.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.ytazwc.rpc.annotations.RpcReference;
import top.ytazwc.rpc.annotations.RpcScan;
import top.ytazwc.rpc.annotations.RpcService;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author 00103943
 * @date 2025-03-25 17:42
 * @package top.ytazwc.rpc.spring
 * @description 实现自定义注解 bean 扫描
 */
@Slf4j
public class RpcRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 扫描类
        ClassPathBeanDefinitionScanner scanner = getScanner(registry);
        scanner.setResourceLoader(resourceLoader);
        // 注册注解
        scanner.addIncludeFilter(new AnnotationTypeFilter(RpcReference.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        // 获取需要扫描的包
        Set<String> basePackages = getBasePackages(importingClassMetadata);
        // 扫描 bean 并管理
        scanner.scan(basePackages.toArray(new String[0]));
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        // 获取 RpcScan 的属性值
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(RpcScan.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        if (Objects.isNull(attributes)) {
            return basePackages;
        }
        // value 值
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        // basePackages
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        // 遍历类
        for (Class<?> clazz : (Class<?>[]) attributes.get("basePackageClasses")) {
            // 返回类所在的包名
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        // 若集合依旧为空 则认为当前注解所在包名为默认值并加入集合
        if (CollectionUtils.isEmpty(basePackages)) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }



        return basePackages;
    }

    // 自定义注解扫描器 用于扫描类路径中的 bean
    protected ClassPathBeanDefinitionScanner getScanner(BeanDefinitionRegistry registry) {
        return new ClassPathBeanDefinitionScanner(registry, false, this.environment, this.resourceLoader) {
            // 重写来确定符合条件的bean
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                // 不是注解；则认为是需要的bean
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }


}
