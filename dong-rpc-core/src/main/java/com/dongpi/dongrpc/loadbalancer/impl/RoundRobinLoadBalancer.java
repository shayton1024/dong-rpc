package com.dongpi.dongrpc.loadbalancer.impl;

import com.dongpi.dongrpc.loadbalancer.LoadBalancer;
import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/10:20
 * @Description: 轮询负载均衡器实现
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * 当前轮询的索引
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()) {
            return null;
        }

        int size = serviceMetaInfoList.size();
        if(size == 1) {
            // 如果只有一个服务，直接返回
            return serviceMetaInfoList.get(0);
        }

        // 取模算法轮询
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
