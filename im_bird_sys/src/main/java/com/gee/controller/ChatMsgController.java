package com.gee.controller;

import com.gee.service.ChatMsgService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ChatMsgController {
    @Resource
    private ChatMsgService chatMsgService;
}
