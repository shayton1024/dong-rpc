package com.dongpi.dongrpc.fault.tolerant.impl;

import cn.hutool.core.collection.CollUtil;
import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.fault.retry.RetryStrategy;
import com.dongpi.dongrpc.fault.retry.RetryStrategyFactory;
import com.dongpi.dongrpc.fault.tolerant.CircuitBreaker;
import com.dongpi.dongrpc.fault.tolerant.TolerantStrategy;
import com.dongpi.dongrpc.loadbalancer.LoadBalancer;
import com.dongpi.dongrpc.loadbalancer.LoadBalancerFactory;
import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import com.dongpi.dongrpc.server.tcp.VertxTcpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/11:43
 * @Description: 转移到其他节点
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 获取其他节点并且调用
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get("serviceMetaInfoList");
        ServiceMetaInfo selectedServiceMetaInfo = (ServiceMetaInfo) context.get("serviceMetaInfo");

        // 移除失败节点
        removeFailNode(selectedServiceMetaInfo, serviceMetaInfoList);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());

        // 获取熔断器实例
        CircuitBreaker circuitBreaker = CircuitBreakerManager.getInstance();

        Map<String, Object> requestParamMap = new HashMap<>();
        requestParamMap.put("methodName", rpcRequest.getMethodName());

        RpcResponse rpcResponse = null;
        while(!serviceMetaInfoList.isEmpty() || rpcResponse == null) {
            ServiceMetaInfo currentServiceMetaInfo = loadBalancer.select(requestParamMap, serviceMetaInfoList);

            // 熔断检测
            if(!circuitBreaker.isOpen(currentServiceMetaInfo)) {
                System.out.println("熔断器打开，跳过节点：" + currentServiceMetaInfo);
                removeFailNode(currentServiceMetaInfo, serviceMetaInfoList);
                continue;
            }

            System.out.println("获取节点" + currentServiceMetaInfo);
            try {
                // 发送tcp请求
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest, currentServiceMetaInfo));

                // 成功后记录熔断器状态
                circuitBreaker.recordSuccess(currentServiceMetaInfo);
                return rpcResponse;
            } catch (Exception exception) {
                // 移除失败节点
                circuitBreaker.recordFailure(currentServiceMetaInfo);
                removeFailNode(currentServiceMetaInfo, serviceMetaInfoList);
            }
        }
        // 所有节点失败后，调用降级
        return doFallback(rpcRequest, e);
    }

    /**
     * 移除失败节点
     * @param currentServiceMetaInfo 当前失败的服务节点
     * @param serviceMetaInfoList 可用的服务节点列表
     */
    private void removeFailNode(ServiceMetaInfo currentServiceMetaInfo, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(CollUtil.isNotEmpty(serviceMetaInfoList)) {
            serviceMetaInfoList.removeIf(serviceMetaInfo -> serviceMetaInfo.getServiceKey().equals(currentServiceMetaInfo.getServiceKey()));
            System.out.println("移除失败节点：" + currentServiceMetaInfo);
        } else {
            System.out.println("没有可用节点了");
        }
    }

    /**
     * 执行降级逻辑
     * @param rpcRequest RPC请求
     * @param e 导致降级的异常
     * @return 降级后的响应
     */
    private RpcResponse doFallback(RpcRequest rpcRequest, Exception e) {
        System.out.println("所有节点失败，进入降级逻辑：" + rpcRequest.getMethodName());

        // 创建降级响应
        RpcResponse rpcResponse = new RpcResponse();

        // 根据方法名、服务名等选择不同降级方案
        String serviceKey = rpcRequest.getServiceName() + ":" + rpcRequest.getServiceVersion();
        String methodName = rpcRequest.getMethodName();

        // 针对不同服务和方法的降级策略
        if ("getUser".equals(methodName)) {
            // 用户服务降级 - 返回默认用户
            Map<String, Object> defaultUser = new HashMap<>();
            defaultUser.put("id", -1);
            defaultUser.put("name", "默认用户");
            defaultUser.put("description", "系统降级");
            rpcResponse.setData(defaultUser);
        } else if (methodName.startsWith("get") || methodName.startsWith("query") || methodName.startsWith("find")) {
            // 查询类接口降级 - 返回空结果
            rpcResponse.setData(null);
        } else if (methodName.startsWith("save") || methodName.startsWith("update") || methodName.startsWith("delete")) {
            // 写操作类接口降级 - 返回失败结果
            rpcResponse.setData(false);
        } else {
            // 通用降级处理 - 返回null并记录异常信息
            rpcResponse.setData(null);
            rpcResponse.setMessage("服务暂时不可用，请稍后重试");
        }

        // 记录降级日志
        System.out.println("服务" + serviceKey + "的方法" + methodName + "已降级，原因：" + e.getMessage());

        return rpcResponse;
    }
}
