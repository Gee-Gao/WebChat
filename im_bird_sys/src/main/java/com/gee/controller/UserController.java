package com.gee.controller;

import com.gee.pojo.User;
import com.gee.service.UserService;
import com.gee.utils.IWdzlJSONResult;
import com.gee.utils.MD5Utils;
import com.gee.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

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
            user.setNickname("Gee");
            user.setPassword(MD5Utils.getPwd(user.getPassword()));
            userResult = userService.insert(user);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userResult, userVo);
        return IWdzlJSONResult.ok(userVo);
    }
}
