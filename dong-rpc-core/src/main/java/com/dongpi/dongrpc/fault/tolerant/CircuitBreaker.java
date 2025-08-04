package com.dongpi.dongrpc.fault.tolerant;

import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/14:12
 * @Description:
 */
public class CircuitBreaker {
    private final int failureThreshold; // 失败阈值
    private final long openTimeMillis; // 打开状态持续时间

    private final Map<String, AtomicInteger> failureCounts = new ConcurrentHashMap<>(); // 失败计数
    private final Map<String, Long> openTimestamps = new ConcurrentHashMap<>(); // 打开状态时间戳

    public CircuitBreaker(int failureThreshold, long openTimeMillis) {
        this.failureThreshold = failureThreshold;
        this.openTimeMillis = openTimeMillis;
    }

    public boolean isOpen(ServiceMetaInfo serviceMetaInfo) {
        String key = serviceMetaInfo.getServiceNodeKey();
        if(!openTimestamps.containsKey(key))
            return true;

        Long openTime = openTimestamps.get(key);
        if(System.currentTimeMillis() - openTime > openTimeMillis) {
            // 半开状态，允许探测一次
            failureCounts.put(key, new AtomicInteger(0));
            openTimestamps.remove(serviceMetaInfo.getServiceNodeKey());
            return true;
        }
        return false;
    }

    public void recordSuccess(ServiceMetaInfo serviceMetaInfo) {
        String key = serviceMetaInfo.getServiceNodeKey();
        openTimestamps.remove(key); // 成功后移除失败计数
    }

    public void recordFailure(ServiceMetaInfo serviceMetaInfo) {
        String key = serviceMetaInfo.getServiceNodeKey();
        AtomicInteger count = failureCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        if(count.incrementAndGet() > failureThreshold) {
            openTimestamps.put(key, System.currentTimeMillis());
        }
    }
}
