package com.gee.service.impl;

import com.gee.mapper.ChatMsgMapper;
import com.gee.service.ChatMsgService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class ChatMsgServiceImpl implements ChatMsgService {
    @Resource
    private ChatMsgMapper chatMsgMapper;
}
