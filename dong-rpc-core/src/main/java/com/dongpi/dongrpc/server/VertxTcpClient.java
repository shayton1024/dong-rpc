package com.dongpi.dongrpc.server;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/07/30/18:31
 * @Description:
 */
public class VertxTcpClient {

    public void start() {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Connected to server");
                // 可以在这里发送数据到服务器
                io.vertx.core.net.NetSocket socket = result.result();
                socket.write(Buffer.buffer("Hello World!"));
                socket.handler(buffer -> {
                   System.out.println("Received response: " + buffer.toString());
                });
            } else {
                System.out.println("Failed to connect to server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpClient client = new VertxTcpClient();
        client.start();
    }
}
