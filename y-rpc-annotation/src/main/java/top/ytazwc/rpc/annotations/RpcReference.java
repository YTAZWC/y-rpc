package top.ytazwc.rpc.annotations;

import java.lang.annotation.*;

/**
 * @author 00103943
 * @date 2025-03-25 10:35
 * @package top.ytazwc.rpc.annotations
 * @description 消费服务注解
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    // 服务版本号
    String version() default "";

    // 服务组
    String group() default "";

}
