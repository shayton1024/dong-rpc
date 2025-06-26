package com.dongpi.example.provider;

import com.dongpi.common.model.User;
import com.dongpi.common.service.UserService;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: shayton
 * @Date: 2025/06/24/13:47
 * @Description:
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
