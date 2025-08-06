package com.dongpi.dongrpc.springboot.starter.bootstrap;

import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.server.tcp.VertxTcpClient;
import com.dongpi.dongrpc.server.tcp.VertxTcpServer;
import com.dongpi.dongrpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/06/15:48
 * @Description: 全局启动类
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取EnableRpc获取的注解属性
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            // 启动Tcp服务
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            // 如果不需要服务端，则不启动服务器
            log.info("RPC服务端未启动，仅启用客户端功能");
        }

    }
}
