package com.dongpi.example.provider;

import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.config.RegistryConfig;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import com.dongpi.dongrpc.registry.LocalRegistry;
import com.dongpi.dongrpc.registry.Registry;
import com.dongpi.dongrpc.registry.RegistryFactory;
import com.dongpi.dongrpc.server.HttpServer;
import com.dongpi.dongrpc.server.VertxHttpServer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/27/11:03
 * @Description:
 */
public class ProviderExample {

    public static void main(String[] args) {

        // 初始化配置对象
        RpcConfig rpc = RpcApplication.getRpcConfig();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

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

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(rpc.getServerPort());
    }
}
