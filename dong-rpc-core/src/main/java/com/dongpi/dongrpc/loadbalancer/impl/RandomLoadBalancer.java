package com.dongpi.dongrpc.loadbalancer.impl;

import com.dongpi.dongrpc.loadbalancer.LoadBalancer;
import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/10:25
 * @Description:
 */
public class RandomLoadBalancer implements LoadBalancer {

    /**
     * 生成随机数
     */
    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if (size == 0) {
            return null; // 如果没有可用服务，返回 null
        }

        if(size == 1) {
            // 如果只有一个服务，直接返回
            return serviceMetaInfoList.get(0);
        }

        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
