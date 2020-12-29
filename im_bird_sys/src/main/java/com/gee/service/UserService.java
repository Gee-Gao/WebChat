package com.gee.service;

import com.gee.pojo.User;

public interface UserService {
    //根据用户名判断用户是否存在
    User queryUserNameIsExit(String username);

    User insert(User user);
}
