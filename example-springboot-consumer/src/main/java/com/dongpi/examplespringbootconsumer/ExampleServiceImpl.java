package com.dongpi.examplespringbootconsumer;

import com.dongpi.common.model.User;
import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/06/16:35
 * @Description:
 */
@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test () {
        User user = new User();
        user.setName("shayton");
        User serviceUser = userService.getUser(user);

        System.out.println(serviceUser.getName());
    }
}
