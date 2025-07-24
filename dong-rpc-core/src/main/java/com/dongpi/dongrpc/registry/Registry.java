package com.dongpi.dongrpc.registry;

import com.dongpi.dongrpc.config.RegistryConfig;
import com.dongpi.dongrpc.model.ServiceMetaInfo;

import java.util.List;


public interface Registry {

    /**
     * 初始化
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 服务注册
     * @param serviceMetaInfo
     * @throws Exception
     */
    void registry(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务
     * @param serviceMetaInfo
     */
    void unRegistry(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceName);

    /**
     * 服务销毁
     */
    void destroy();
}
