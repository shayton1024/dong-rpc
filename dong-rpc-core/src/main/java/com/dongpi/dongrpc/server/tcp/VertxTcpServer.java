package com.dongpi.dongrpc.server.tcp;

import com.dongpi.dongrpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/30/18:19
 * @Description:
 */
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] request) {
        // 这里可以添加自定义的请求处理逻辑
        // 例如解析请求、调用服务、构造响应等
        // 返回响应数据
        return "Hello, client".getBytes(); // 示例返回空字节数组
    }

    @Override
    public void doStart(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        // 创建TCP服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(new TcpServerHandler());

        // 启动TCP服务器并且监听端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP Server started on port " + port);
            } else {
                System.out.println("Failed to start TCP Server on port " + port + ": " + result.cause());
            }
        });
    }

}
