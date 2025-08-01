package com.dongpi.dongrpc.loadbalancer;

import com.dongpi.dongrpc.loadbalancer.impl.RoundRobinLoadBalancer;
import com.dongpi.dongrpc.spi.SpiLoader;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/10:51
 * @Description: 负载均衡器工厂类
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取负载均衡器实例
     *
     * @return 负载均衡器实例
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
