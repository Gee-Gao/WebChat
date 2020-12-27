package com.gee.service.impl;

import com.gee.mapper.UserMapper;
import com.gee.service.UserService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
}
