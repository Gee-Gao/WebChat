package com.gee.service.impl;

import com.gee.enums.SearchFriendsStatusEnum;
import com.gee.mapper.FriendsRequestMapper;
import com.gee.mapper.MyFriendsMapper;
import com.gee.mapper.UserMapper;
import com.gee.pojo.FriendsRequest;
import com.gee.pojo.MyFriends;
import com.gee.pojo.User;
import com.gee.service.UserService;
import com.gee.utils.FastDFSClient;
import com.gee.utils.FileUtils;
import com.gee.utils.QRCodeUtils;
import com.gee.utils.Sid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private QRCodeUtils qrCodeUtils;
    @Resource
    private FastDFSClient fastDFSClient;
    @Resource
    private MyFriendsMapper myFriendsMapper;
    @Resource
    private Sid sid;
    @Resource
    private FriendsRequestMapper friendsRequestMapper;

    //根据用户名判断用户是否存在
    @Override
    public User queryUserNameIsExit(String username) {
        return userMapper.queryUserNameIsExit(username);
    }

    @Override
    public User updateUserInfo(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return userMapper.selectByPrimaryKey(user.getId());
    }

    //搜索好友前置条件
    @Override
    public Integer preconditionSearchFriend(String myUserId, String friendUserName) {
        User user = userMapper.queryUserNameIsExit(friendUserName);
        //如果搜索结果为空，返回无此用户
        if (user == null)
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        //如果搜索结果是自己，返回不能添加自己
        if (myUserId.equals(user.getId()))
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        //如果搜索结果已经是自己好友，返回已经是好友
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(myUserId);
        myFriends.setMyFriendUserId(user.getId());
        MyFriends friends = myFriendsMapper.selectOneByExample(myFriends);
        if (friends != null)
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    //发送好友请求
    @Override
    public void sendFriendRequest(String myUserId, String friendUserName) {
        User user = userMapper.queryUserNameIsExit(friendUserName);
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(myUserId);
        myFriends.setMyFriendUserId(user.getId());
        MyFriends friends = myFriendsMapper.selectOneByExample(myFriends);
        if (friends == null) {
            FriendsRequest friendsRequest = new FriendsRequest();
            String requestId = sid.nextShort();
            friendsRequest.setId(requestId);
            friendsRequest.setSendUserId(myUserId);
            friendsRequest.setAcceptUserId(user.getId());
            friendsRequest.setRequestDateTime(new Date());
            friendsRequestMapper.insert(friendsRequest);
        }
    }

    @Override
    public User insert(User user) {
        user.setId(sid.nextShort());
        //为每一个用户生成唯一二维码
        String qrCodePath = "D:/user" + user.getId() + "qrcode.png";
        qrCodeUtils.createQRCode(qrCodePath, "bird_qrcode:" + user.getUsername());
        MultipartFile qrcodeFile = FileUtils.fileToMultipart(qrCodePath);
        String qrcodeUrl = null;
        try {
            qrcodeUrl = fastDFSClient.uploadQRCode(qrcodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrcodeUrl);
        userMapper.insert(user);
        return user;
    }
}
