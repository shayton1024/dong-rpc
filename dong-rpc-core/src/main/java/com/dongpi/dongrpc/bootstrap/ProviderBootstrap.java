package com.dongpi.dongrpc.bootstrap;

import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.config.RegistryConfig;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import com.dongpi.dongrpc.model.ServiceRegisterInfo;
import com.dongpi.dongrpc.registry.LocalRegistry;
import com.dongpi.dongrpc.registry.Registry;
import com.dongpi.dongrpc.registry.RegistryFactory;
import com.dongpi.dongrpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/05/17:40
 * @Description: 服务提供者初始化
 */
public class ProviderBootstrap {

    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {

        // 框架初始化
        RpcApplication.init();

        // 配置全局对象
        final RpcConfig rpc = RpcApplication.getRpcConfig();

        // 注册服务
        for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {

            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpc.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpc.getServerHost());
            serviceMetaInfo.setServicePort(rpc.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("服务注册失败", e);
            }
        }

        // 启动Tcp服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpc.getServerPort());
    }
}
