package com.gee.netty;

import java.io.Serializable;

public class ChatContent implements Serializable {
    //发送者id
    private String senderId;
    //接收者id
    private String receiverId;
    //聊天内容
    private String msg;
    //消息id用于消息签收
    private String msgId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
