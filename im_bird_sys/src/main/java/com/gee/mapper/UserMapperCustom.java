package com.gee.mapper;

import com.gee.vo.FriendsRequestVo;

import java.util.List;

public interface UserMapperCustom {
    //查询添加好友请求列表
    List<FriendsRequestVo> queryFriendRequestList(String acceptUserId);
}
