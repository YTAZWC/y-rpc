package top.ytazwc.rpc.annotations;

import java.lang.annotation.*;

/**
 * @author 00103943
 * @date 2025-03-25 10:22
 * @package top.ytazwc.rpc.annotations
 * @description 服务注册注解
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    // 服务版本号 默认为空
    String version() default "";

    // 服务组 默认为空
    String group() default "";

}
