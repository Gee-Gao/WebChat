package com.gee.service.impl;

import com.gee.mapper.MyFriendsMapper;
import com.gee.service.MyFriendsService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class MyFriendsServiceImpl implements MyFriendsService {
    @Resource
    private MyFriendsMapper myFriendsMapper;
}
