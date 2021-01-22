package com.gee.service;

import com.gee.netty.ChatContent;
import com.gee.pojo.ChatMsg;
import com.gee.pojo.FriendsRequest;
import com.gee.pojo.MyFriends;
import com.gee.pojo.User;
import com.gee.vo.FriendsRequestVo;
import com.gee.vo.MyFriendsVo;

import java.util.List;

public interface UserService {
    //根据用户名判断用户是否存在
    User queryUserNameIsExit(String username);

    //根据id查找用户
    User queryUserById(String userId);

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

    //保存用户聊天消息
    String saveMsg(ChatContent chatContent);

    //批量更新签收状态
    void updateMsgSigned(List<String> msgList);

    //获取未签收的消息列表
    List<ChatMsg> getUnReadMsgList(String acceptUserId);

    //修改好友备注
    void changeRemark(MyFriends myFriends);

    //获取好友备注
    MyFriends getFriendRemark(MyFriends myFriends);
}
