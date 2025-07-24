package com.dongpi.dongrpc.server;

import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.dongpi.dongrpc.registry.LocalRegistry;
import com.dongpi.dongrpc.serializer.JdkSerializer;
import com.dongpi.dongrpc.serializer.Serializer;
import com.dongpi.dongrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/24/16:19
 * @Description: HTTP 请求处理
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        System.out.println(serializer.toString());

        // 记录日志
        System.out.println("Received request:" + request.method() + " " + request.uri());

        // 异步处理http请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 如果是null，直接返回
            if(Objects.isNull(rpcRequest)) {
                rpcResponse.setMessage("rpc request is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try{
                // 获取要调用的服务实现类，使用反射进行调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                // 获取方法
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                // 调用服务方法，返回结果
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 返回结果封装
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");

            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 结果响应
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response().putHeader("content-type", "application/json");

        try {
            // 序列化
            byte[] bytes = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            response.end();
        }
    }
}
