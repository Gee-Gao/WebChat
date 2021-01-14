package com.gee.netty;

import java.io.Serializable;

public class DataContent implements Serializable {
    //动作类型
    private Integer action;
    //用户的聊天内容
    private ChatContent chatContent;
    //扩展字段
    private String extend;

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatContent getChatContent() {
        return chatContent;
    }

    public void setChatContent(ChatContent chatContent) {
        this.chatContent = chatContent;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }


}
