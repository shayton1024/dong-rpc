package com.dongpi.example.provider;

import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.bootstrap.ProviderBootstrap;
import com.dongpi.dongrpc.config.RegistryConfig;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import com.dongpi.dongrpc.model.ServiceRegisterInfo;
import com.dongpi.dongrpc.registry.LocalRegistry;
import com.dongpi.dongrpc.registry.Registry;
import com.dongpi.dongrpc.registry.RegistryFactory;
import com.dongpi.dongrpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/27/11:03
 * @Description:
 */
public class ProviderExample {

    public static void main(String[] args) {
        // 提供服务列表
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
