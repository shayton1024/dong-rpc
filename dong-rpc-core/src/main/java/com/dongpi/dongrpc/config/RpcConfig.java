package com.dongpi.dongrpc.config;

import com.dongpi.dongrpc.fault.retry.RetryStrategyKeys;
import com.dongpi.dongrpc.loadbalancer.LoadBalancerKeys;
import com.dongpi.dongrpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/25/17:07
 * @Description: 框架全局配置，保存配置信息
 */

@Data
public class RpcConfig {

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 名称
     */
    private String name = "dong-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 端口号
     */
    private Integer serverPort = 8081;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;
}
