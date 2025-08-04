package com.dongpi.dongrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.dongpi.dongrpc.RpcApplication;
import com.dongpi.dongrpc.model.RpcRequest;
import com.dongpi.dongrpc.model.RpcResponse;
import com.dongpi.dongrpc.model.ServiceMetaInfo;
import com.dongpi.dongrpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/30/18:31
 * @Description:
 */
public class VertxTcpClient {

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        // 发送Tcp请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            if(result.succeeded()) {
                System.out.println("连接成功，发送请求");
                NetSocket socket = result.result();
                // 发送数据
                // 构造信息
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);
                // 编码请求
                try {
                    Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
                    socket.write(encode);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误", e);
                }
                // 接收响应
                TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                    // 解码响应
                    try {
                        ProtocolMessage<RpcResponse> responseMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseFuture.complete(responseMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息解码错误", e);
                    }
                });
                socket.handler(bufferHandlerWrapper);
            } else {
                System.err.println("Fail to connect to server: " + result.cause().getMessage());
                responseFuture.completeExceptionally(new RuntimeException("Fail to connect to server: " + result.cause().getMessage()));
            }
        });
        RpcResponse rpcResponse = responseFuture.get();
        // 关闭链接
        netClient.close();
        return rpcResponse;
    }
}
