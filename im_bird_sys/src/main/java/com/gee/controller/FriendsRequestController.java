package com.gee.controller;

import com.gee.service.FriendsRequestService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class FriendsRequestController {
    @Resource
    private FriendsRequestService friendsRequestService;
}
