package com.gee.service.impl;

import com.gee.enums.MsgSignFlagEnum;
import com.gee.enums.SearchFriendsStatusEnum;
import com.gee.mapper.*;
import com.gee.netty.ChatContent;
import com.gee.pojo.ChatMsg;
import com.gee.pojo.FriendsRequest;
import com.gee.pojo.MyFriends;
import com.gee.pojo.User;
import com.gee.service.UserService;
import com.gee.utils.FastDFSClient;
import com.gee.utils.FileUtils;
import com.gee.utils.QRCodeUtils;
import com.gee.utils.Sid;
import com.gee.vo.FriendsRequestVo;
import com.gee.vo.MyFriendsVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
    @Resource
    private UserMapperCustom userMapperCustom;
    @Resource
    private ChatMsgMapper chatMsgMapper;


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

    //查询添加好友请求列表
    @Override
    public List<FriendsRequestVo> queryFriendRequestList(String acceptUserId) {
        return userMapperCustom.queryFriendRequestList(acceptUserId);
    }

    //保存用户聊天消息
    @Override
    public String saveMsg(ChatContent chatContent) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setId(sid.nextShort());
        chatMsg.setSendUserId(chatContent.getSenderId());
        chatMsg.setAcceptUserId(chatContent.getReceiverId());
        chatMsg.setCreateTime(new Date());
        chatMsg.setSignFlag(MsgSignFlagEnum.signed.type);
        chatMsg.setMsg(chatContent.getMsg());
        chatMsgMapper.insert(chatMsg);
        return chatMsg.getId();
    }

    //批量更新签收状态
    @Override
    public void updateMsgSigned(List<String> msgList) {
        userMapperCustom.batchUpdateMsgSigned(msgList);
    }

    //处理忽略请求
    @Override
    public void deleteFriendRequest(FriendsRequest friendsRequest) {
        friendsRequestMapper.deleteByFriendRequest(friendsRequest);
    }

    //处理通过请求
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        //进行双向好友绑定
        saveFriend(sendUserId, acceptUserId);
        saveFriend(acceptUserId, sendUserId);

        //删除好友请求表的数据
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setSendUserId(sendUserId);
        friendsRequest.setAcceptUserId(acceptUserId);
        deleteFriendRequest(friendsRequest);
    }

    //查询好友列表
    @Override
    public List<MyFriendsVo> queryMyFriends(String userId) {
        return userMapperCustom.queryMyFriends(userId);
    }

    //添加好友
    private void saveFriend(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(sendUserId);
        myFriends.setMyFriendUserId(acceptUserId);
        myFriends.setId(sid.nextShort());
        myFriendsMapper.insert(myFriends);
    }

    @Override
    public User insert(User user) {
        user.setId(sid.nextShort());
        //为每一个用户生成唯一二维码
        String qrCodePath = "D:/qrcode/user" + user.getId() + "qrcode.png";
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
