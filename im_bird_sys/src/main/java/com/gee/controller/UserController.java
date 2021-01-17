package com.gee.controller;

import com.gee.bo.UserBo;
import com.gee.enums.OperatorFriendRequestTypeEnum;
import com.gee.enums.SearchFriendsStatusEnum;
import com.gee.pojo.ChatMsg;
import com.gee.pojo.FriendsRequest;
import com.gee.pojo.User;
import com.gee.service.UserService;
import com.gee.utils.FastDFSClient;
import com.gee.utils.FileUtils;
import com.gee.utils.IWdzlJSONResult;
import com.gee.utils.MD5Utils;
import com.gee.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private FastDFSClient fastDFSClient;

    //获取未签收消息列表
    @GetMapping("getUnReadMsgList")
    public IWdzlJSONResult getUnReadMsgList(String acceptUserId) {
        //数据校验
        if (StringUtils.isBlank(acceptUserId))
            return IWdzlJSONResult.errorMsg("用户id为空");
        //根据接收者id查找未签收的消息
        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);
        return IWdzlJSONResult.ok(unReadMsgList);
    }

    //好友列表查询
    @GetMapping("myFriends")
    public IWdzlJSONResult myFriends(String userId) {
        //数据校验
        if (StringUtils.isBlank(userId))
            return IWdzlJSONResult.errorMsg("用户id为空");
        //查询好友列表并返回
        return IWdzlJSONResult.ok(userService.queryMyFriends(userId));
    }

    //好友请求处理
    @GetMapping("operFriendRequest")
    public IWdzlJSONResult operFriendRequest(FriendsRequest friendsRequest, Integer operType) {
        //如果请求类型为忽略，删除对应好友请求
        if (OperatorFriendRequestTypeEnum.IGNORE.type == operType) {
            userService.deleteFriendRequest(friendsRequest);
        }
        //如果请求类型为通过，添加好友，同时删除好友请求
        if (OperatorFriendRequestTypeEnum.PASS.type == operType) {
            userService.passFriendRequest(friendsRequest.getSendUserId(), friendsRequest.getAcceptUserId());
        }
        //查询好友列表并返回
        return IWdzlJSONResult.ok(userService.queryMyFriends(friendsRequest.getAcceptUserId()));
    }

    //查询添加好友请求列表
    @GetMapping("queryFriendsRequest")
    public IWdzlJSONResult queryFriendsRequest(String userId) {
        return IWdzlJSONResult.ok(userService.queryFriendRequestList(userId));
    }

    //发送好友请求
    @GetMapping("addFriendRequest")
    public IWdzlJSONResult addFriendRequest(String myUserId, String friendUserName) {
        if (StringUtils.isBlank(myUserId)) {
            return IWdzlJSONResult.errorMsg("用户未登录");
        }
        if (StringUtils.isBlank(friendUserName)) {
            return IWdzlJSONResult.errorMsg("搜索好友账号不能为空");
        }

        Integer status = userService.preconditionSearchFriend(myUserId, friendUserName);
        if (status.equals(SearchFriendsStatusEnum.SUCCESS.status)) {
            userService.sendFriendRequest(myUserId, friendUserName);
            return IWdzlJSONResult.ok();
        } else {
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IWdzlJSONResult.errorMsg(msg);
        }
    }

    //搜索好友
    @GetMapping("searchFriend")
    public IWdzlJSONResult searchFriend(String myUserId, String friendUserName) {
        if (StringUtils.isBlank(myUserId)) {
            return IWdzlJSONResult.errorMsg("用户未登录");
        }
        if (StringUtils.isBlank(friendUserName)) {
            return IWdzlJSONResult.errorMsg("搜索好友账号不能为空");
        }
        Integer status = userService.preconditionSearchFriend(myUserId, friendUserName);
        if (status.equals(SearchFriendsStatusEnum.SUCCESS.status)) {
            User user = userService.queryUserNameIsExit(friendUserName);
            //复制对象信息
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            //返回查询到的用户
            return IWdzlJSONResult.ok(userVo);
        } else {
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IWdzlJSONResult.errorMsg(msg);
        }
    }

    //修改昵称
    @PostMapping("/updateNickname")
    public IWdzlJSONResult updateNickname(User user) {
        User userResult = userService.updateUserInfo(user);
        return IWdzlJSONResult.ok(userResult);
    }

    //头像上传
    @PostMapping("/uploadFaceBase64")
    public IWdzlJSONResult uploadAvatar(@RequestBody UserBo userBo) throws Exception {
        //获取前端传过来的base64字符串，然后转为文件对象进行上传
        String faceData = userBo.getFaceData();

        //获取项目根路径，并生成头像上传临时路径
        String path = this.getClass().getResource("/").getPath().substring(1) + "avatar/";

        File existsFile = new File(path);
        if (!existsFile.exists()) {
            existsFile.mkdirs();
        }

        String userFacePath = path + userBo.getUserId() + "userFaceBase64.png";
        //调用FileUtils将userFacePath转为文件对象
        FileUtils.base64ToFile(userFacePath, faceData);
        MultipartFile file = FileUtils.fileToMultipart(userFacePath);

        //获取fastdfs上传图片的路径
        String url = fastDFSClient.uploadBase64(file);
        String thump = "_150x150.";
        String[] split = url.split("\\.");

        String thumpUrl = split[0] + thump + split[1];

        //更新用户头像
        User user = new User();
        user.setFaceImage(thumpUrl);
        user.setId(userBo.getUserId());
        user.setFaceImageBig(url);
        User result = userService.updateUserInfo(user);
        return IWdzlJSONResult.ok(result);
    }

    //用户登录注册一体化方法
    @PostMapping("/registerOrLogin")
    public IWdzlJSONResult registerOrLogin(User user) {
        User userResult = userService.queryUserNameIsExit(user.getUsername());

        if (userResult != null) {  //用户存在
            //判断密码是否正确
            if (!userResult.getPassword().equals(MD5Utils.getPwd(user.getPassword()))) {
                return IWdzlJSONResult.errorMsg("密码不正确");
            }
        } else { //用户不存在
            StringBuilder stringBuilder = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 8; i++) {
                stringBuilder.append(random.nextInt(10));
            }
            user.setNickname("麻雀用户: " + stringBuilder.toString());
            user.setFaceImage("M00/00/00/rBGOWF_6cqSAa3LvAAbsRdJj-KE268_150x150.png");
            user.setFaceImageBig("M00/00/00/rBGOWF_6cqSAa3LvAAbsRdJj-KE268.png");
            user.setPassword(MD5Utils.getPwd(user.getPassword()));
            userResult = userService.insert(user);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userResult, userVo);
        return IWdzlJSONResult.ok(userVo);
    }
}
