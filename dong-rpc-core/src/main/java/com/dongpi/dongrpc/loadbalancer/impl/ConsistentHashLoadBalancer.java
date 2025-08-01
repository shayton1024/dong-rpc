package com.dongpi.dongrpc.loadbalancer.impl;

import com.dongpi.dongrpc.loadbalancer.LoadBalancer;
import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/01/10:27
 * @Description: 一致性hash负载均衡器实现
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 虚拟节点映射，key为虚拟节点名称，value为服务元信息
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数量
     * 这里设置为100个虚拟节点，可以根据实际情况调整
     */
    private final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建虚拟节点
        for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for(int i = 0;i < VIRTUAL_NODE_NUM;i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 获取请求的hash值
        int hash = getHash(requestParams);

        // 选择大于等于最接近的hash值虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry == null) {
            // 如果没有找到大于等于的hash值，返回第一个虚拟节点
            entry = virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    /**
     * 计算哈希值 (FNV-1a算法)
     * @param key 需要哈希的对象
     * @return 哈希值
     */
    private int getHash(Object key) {
        String keyStr = key.toString();
        final int p = 16777619;
        int hash = (int)2166136261L;

        for (int i = 0; i < keyStr.length(); i++) {
            hash = (hash ^ keyStr.charAt(i)) * p;
        }

        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 确保哈希值为正数
        return hash & 0x7FFFFFFF;
    }

//    private int getHash(Object key) {
//        return key.hashCode();
//    }
}
