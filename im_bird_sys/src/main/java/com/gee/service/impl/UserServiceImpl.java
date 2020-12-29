package com.gee.service.impl;

import com.gee.mapper.UserMapper;
import com.gee.pojo.User;
import com.gee.service.UserService;
import com.gee.utils.Sid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    Sid sid;

    //根据用户名判断用户是否存在
    @Override
    public User queryUserNameIsExit(String username) {
        return userMapper.queryUserNameIsExit(username);
    }

    @Override
    public User insert(User user) {
        user.setId(sid.nextShort());
        userMapper.insert(user);
        return user;
    }
}
