package com.dongpi.example.consumer;

import com.dongpi.common.model.User;
import com.dongpi.common.service.UserService;
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

//        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//
//        System.out.println(rpcConfig);

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("shayton");

        // 调用
        User newUser = userService.getUser(user);
        if(newUser != null) {
            System.out.println(newUser.toString());
        } else {
            System.out.println("user == null");
        }

        Integer number = userService.getNumber();
        System.out.println(number);
    }
}
