package com.dongpi.examplespringbootprovider;

import com.dongpi.common.model.User;
import com.dongpi.common.service.UserService;
import com.dongpi.dongrpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/08/06/16:32
 * @Description:
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println(user.getName());
        return user;
    }

    public int getNumber() {
        return UserService.super.getNumber();
    }
}
