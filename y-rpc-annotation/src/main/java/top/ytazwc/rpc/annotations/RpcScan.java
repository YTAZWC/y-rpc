package top.ytazwc.rpc.annotations;

import org.springframework.context.annotation.Import;
import top.ytazwc.rpc.spring.RpcRegistrar;

import java.lang.annotation.*;

/**
 * @author 00103943
 * @date 2025-03-25 17:40
 * @package top.ytazwc.rpc.annotations
 * @description 自定义bean扫描注解
 */
@Documented
@Target({ElementType.TYPE})
@Import({RpcRegistrar.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcScan {
}
