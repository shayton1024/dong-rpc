package com.dongpi.dongrpc.registry;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/29/14:48
 * @Description:
 */

import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    private Map<String, Map<String, ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     * @return 服务缓存
     */
    void writeCache(String serviceKey, String serviceNodeKey, ServiceMetaInfo serviceMetaInfos) {
        if (serviceKey == null || serviceNodeKey == null || serviceMetaInfos == null) {
            throw new IllegalArgumentException("Service key, mode key and meta info cannot be null");
        }

        // 获取或创建服务缓存
        Map<String, ServiceMetaInfo> nodeCache = serviceCache.computeIfAbsent(serviceKey, k -> new ConcurrentHashMap<>());
        // 写入服务元信息
        nodeCache.put(serviceNodeKey, serviceMetaInfos);
    }

    /**
     * 读缓存
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        // 获取服务缓存
        Map<String, ServiceMetaInfo> nodeCache = serviceCache.get(serviceKey);
        if (nodeCache == null) {
            return null; // 返回空列表表示没有缓存
        }

        // 返回服务元信息列表
        return new ArrayList<>(nodeCache.values());
    }

    /**
     * 清除缓存
     */
    void clearCache(String serviceKey, String serviceNodeKey) {
        if (serviceKey == null || serviceNodeKey == null) {
            return;
        }

        // 获取服务缓存
        Map<String, ServiceMetaInfo> nodeCache = serviceCache.get(serviceKey);
        if (nodeCache != null) {
            // 清除指定节点的缓存
            nodeCache.remove(serviceNodeKey);
            // 如果节点缓存为空，则移除整个服务缓存
            if (nodeCache.isEmpty()) {
                serviceCache.remove(serviceKey);
            }
        }
    }

    public boolean containsCache(String serviceKey, String serviceNodeKey) {
        Map<String, ServiceMetaInfo> metaInfoMap = serviceCache.get(serviceKey);
        if(metaInfoMap == null){
            return false;
        }

        return metaInfoMap.containsKey(serviceNodeKey);
    }

    public boolean containsKey(String serviceKey) {
        return serviceCache.containsKey(serviceKey);
    }
}
