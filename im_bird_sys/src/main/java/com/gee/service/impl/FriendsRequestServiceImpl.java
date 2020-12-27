package com.gee.service.impl;

import com.gee.mapper.MyFriendsMapper;
import com.gee.service.FriendsRequestService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class FriendsRequestServiceImpl implements FriendsRequestService {
    @Resource
    private MyFriendsMapper friendsMapper;
}
