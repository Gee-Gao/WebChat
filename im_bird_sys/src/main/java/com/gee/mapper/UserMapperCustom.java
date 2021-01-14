package com.gee.mapper;

import com.gee.vo.FriendsRequestVo;
import com.gee.vo.MyFriendsVo;

import java.util.List;

public interface UserMapperCustom {
    //查询添加好友请求列表
    List<FriendsRequestVo> queryFriendRequestList(String acceptUserId);

    //查询好友列表
    List<MyFriendsVo> queryMyFriends(String userId);

    //批量更新签收状态
    void batchUpdateMsgSigned(List<String> msgIdList);
}
