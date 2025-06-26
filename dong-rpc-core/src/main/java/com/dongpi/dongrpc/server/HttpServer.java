package com.dongpi.dongrpc.server;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/24/14:03
 * @Description: Http 服务器接口
 */
public interface HttpServer {
    /**
     * 启动服务器
     */

    void doStart(int port);
}
