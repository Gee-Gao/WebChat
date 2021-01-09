package com.gee.service;

import com.gee.pojo.User;

public interface UserService {
    //根据用户名判断用户是否存在
    User queryUserNameIsExit(String username);

    User insert(User user);

    User updateUserInfo(User user);

    //搜索好友前置条件
    Integer preconditionSearchFriend(String myUserId, String friendUserName);

    //发送好友请求
    void sendFriendRequest(String myUserId, String friendUserName);
}
