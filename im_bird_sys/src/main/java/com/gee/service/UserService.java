package com.gee.service;

import com.gee.pojo.FriendsRequest;
import com.gee.pojo.User;
import com.gee.vo.FriendsRequestVo;
import com.gee.vo.MyFriendsVo;

import java.util.List;

public interface UserService {
    //根据用户名判断用户是否存在
    User queryUserNameIsExit(String username);

    User insert(User user);

    User updateUserInfo(User user);

    //搜索好友前置条件
    Integer preconditionSearchFriend(String myUserId, String friendUserName);

    //发送好友请求
    void sendFriendRequest(String myUserId, String friendUserName);

    //查询添加好友请求列表
    List<FriendsRequestVo> queryFriendRequestList(String acceptUserId);

    //处理忽略请求
    void deleteFriendRequest(FriendsRequest friendsRequest);

    //处理通过请求
    void passFriendRequest(String sendUserId, String acceptUserId);

    //查询好友列表
    List<MyFriendsVo> queryMyFriends(String userId);
}
