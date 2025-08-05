package com.dongpi.dongrpc.bootstrap;

import com.dongpi.dongrpc.RpcApplication;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/05/18:00
 * @Description: 消费者引导类
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init() {
        // Rpc框架初始化
        RpcApplication.init();
    }
}
