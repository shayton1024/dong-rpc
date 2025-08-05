package com.dongpi.example.consumer;

import com.dongpi.common.model.User;
import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.bootstrap.ConsumerBootstrap;
import com.dongpi.dongrpc.config.RpcConfig;
import com.dongpi.dongrpc.proxy.ServiceProxyFactory;
import com.dongpi.dongrpc.utils.ConfigUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/27/10:44
 * @Description:
 */
public class ConsumerExample {

    public static void main(String[] args) {
        // 初始化配置
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("shayton");

        // 调用服务
        User result = userService.getUser(user);
        if(result != null) {
            System.out.println("调用结果: " + result);
        } else {
            System.out.println("调用失败，未返回结果");
        }
    }
}
