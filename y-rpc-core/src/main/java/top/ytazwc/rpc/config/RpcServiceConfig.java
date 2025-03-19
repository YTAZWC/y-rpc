package top.ytazwc.rpc.config;

import lombok.*;

/**
 * @author 00103943
 * @date 2025-03-19 13:56
 * @package top.ytazwc.rpc.config
 * @Description rpc 服务配置类
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcServiceConfig {

    // 服务版本
    private String version = "";

    // 服务分组
    private String group = "";

    // 目标服务
    private Object service;

    /**
     * 获取目标服务的接口的完整名称
     * @return 接口完成名称
     */
    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

    /**
     * 获取调用服务的唯一标识：接口名称+服务所属组+服务版本
     * @return 服务唯一标识
     */
    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

}
