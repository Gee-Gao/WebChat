package com.gee.netty;

import io.netty.channel.Channel;

import java.util.HashMap;

public class UserChannelRel {
    private static HashMap<String, Channel> manage = new HashMap<>();

    public static void put(String senderId, Channel channel) {
        manage.put(senderId, channel);
    }

    public static Channel get(String senderId) {
        return manage.get(senderId);
    }
}
