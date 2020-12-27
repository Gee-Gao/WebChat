package com.gee.controller;

import com.gee.service.MyFriendsService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MyFriendsController {
    @Resource
    private MyFriendsService myFriendsService;
}
