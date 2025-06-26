package com.dongpi.example.consumer;

import com.dongpi.common.model.User;
import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.proxy.ServiceProxyFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/24/13:50
 * @Description:
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        // 获取UserService的实现类对象
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("shayton");

        // 调用
        User newUser = userService.getUser(user);
        if(newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
