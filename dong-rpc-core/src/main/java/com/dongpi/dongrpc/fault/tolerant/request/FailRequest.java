package com.dongpi.dongrpc.fault.tolerant.request;

import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/04/15:21
 * @Description:
 */
@Data
public class FailRequest {
    private final RpcRequest rpcRequest;
    private final ServiceMetaInfo serviceMetaInfo;
    private final List<ServiceMetaInfo> serviceMetaInfoList;

    public FailRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo, List<ServiceMetaInfo> serviceMetaInfoList) {
        this.rpcRequest = rpcRequest;
        this.serviceMetaInfo = serviceMetaInfo;
        this.serviceMetaInfoList = serviceMetaInfoList;
    }
}
