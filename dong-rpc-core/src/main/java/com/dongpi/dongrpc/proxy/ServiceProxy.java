package com.dongpi.dongrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.dongpi.constant.RpcConstant;
import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import com.dongpi.dongrpc.registry.Registry;
import com.dongpi.dongrpc.registry.RegistryFactory;
import com.dongpi.dongrpc.serializer.JdkSerializer;
import com.dongpi.dongrpc.serializer.Serializer;
import com.dongpi.dongrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/25/15:15
 * @Description: 服务代理（JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 序列化
            byte[] bytes = serializer.serialize(rpcRequest);

            // 从注册中心获取服务提供者的请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(rpcRequest.getServiceName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }

            // 暂时先取第一个服务地址
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 发送请求
            try(HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                        .body(bytes)
                        .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列户
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
