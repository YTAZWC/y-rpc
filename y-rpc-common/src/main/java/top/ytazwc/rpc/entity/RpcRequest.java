package top.ytazwc.rpc.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @author 00103943
 * @date 2025-03-18 15:40
 * @package top.ytazwc.rpc.entity
 * @Description rpc 请求实体类
 */
@Getter                 // 请求类暂不开发set接口
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    // 序列化版本
    private static final long serialVersionUID = 1L;

    // 请求id
    private String requestId;

    // 调用服务的接口名称
    private String interfaceName;

    // 调用接口的方法名称
    private String methodName;

    // 调用方法的参数
    private Object[] parameters;

    // 调用方法的参数类型
    private Class<?>[] paramTypes;

    // 服务版本号
    private String version;

    // 所属组
    private String group;

    /**
     * 通过调用服务名 + 及服务所属组 + 服务所属版本 = 确定唯一调用服务
     * @return 返回唯一服务名称
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + ":" + this.getGroup() + ":" + this.getVersion();
    }

}
