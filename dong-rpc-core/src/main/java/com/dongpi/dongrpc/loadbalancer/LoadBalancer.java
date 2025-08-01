package com.dongpi.dongrpc.loadbalancer;

import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/10:18
 * @Description: 负载均衡器（消费端使用）
 */
public interface LoadBalancer {
    /**
     * 选择服务调用
     * @param requestParams
     * @param serviceMetaInfoList
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
