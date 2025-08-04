package com.dongpi.dongrpc.fault.tolerant.impl;

import com.dongpi.dongrpc.fault.tolerant.TolerantStrategy;
import com.dongpi.dongrpc.fault.tolerant.request.FailRequest;
import com.dongpi.dongrpc.fault.tolerant.request.FailRequestQueue;
import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/11:43
 * @Description: 故障恢复，降级处理
 */
public class FailBackTolerantStrategy implements TolerantStrategy {

    private final FailRequestQueue failRequestQueue = FailRequestQueue.getInstance();

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");
        ServiceMetaInfo serviceMetaInfo = (ServiceMetaInfo) context.get("selectedServiceMetaInfo");
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get("serviceMetaInfoList");

        System.err.println("服务调用失败，进行故障恢复处理：" + rpcRequest.getMethodName());

        // 加入失败请求队列
        failRequestQueue.add(new FailRequest(rpcRequest, serviceMetaInfo, serviceMetaInfoList));

        // 返回默认响应
        return new RpcResponse();
    }
}
