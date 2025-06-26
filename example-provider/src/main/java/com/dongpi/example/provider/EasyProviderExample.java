package com.dongpi.example.provider;

import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.registry.LocalRegistry;
import com.dongpi.dongrpc.server.HttpServer;
import com.dongpi.dongrpc.server.VertxHttpServer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/24/13:49
 * @Description:
 */
public class EasyProviderExample {
    public static void main(String[] args) {

        // 服务注册
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8081);
    }
}
