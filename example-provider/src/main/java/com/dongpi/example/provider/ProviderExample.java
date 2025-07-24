package com.dongpi.example.provider;

import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.registry.LocalRegistry;
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
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(rpc.getServerPort());
    }
}
