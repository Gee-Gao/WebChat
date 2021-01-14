package com.gee.netty;

import com.gee.enums.MsgActionEnum;
import com.gee.service.UserService;
import com.gee.utils.JsonUtils;
import com.gee.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理消息的handler
 * 由于它的传输数据的载体是frame，这个frame 在netty中，是用于为websocket专门处理文本对象的，frame是消息的载体，此类叫：TextWebSocketFrame
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        //获取客户端发送的消息
        String content = msg.text();
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Channel channel = ctx.channel();
        //判断消息类型，不同类型处理不同业务
        Integer action = dataContent.getAction();
        //当第一次open时，初始化channel，将channel和userId进行绑定
        if (MsgActionEnum.CONNECT.type == action) {
            String senderId = dataContent.getChatContent().getSenderId();
            UserChannelRel.put(senderId, channel);
        }
        //如果是聊天类型的消息，
        else if (MsgActionEnum.CHAT.type == action) {
            ChatContent chatContent = dataContent.getChatContent();
            String receiverId = chatContent.getReceiverId();
            //把聊天记录保存到数据库，同时签收状态为【未签收】
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            String msgId = userService.saveMsg(chatContent);
            chatContent.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatContent(chatContent);

            //发送消息
            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel != null) {
                //从ChannelGroup users查找对应的channel是否存在
                Channel findChannel = users.find(receiverChannel.id());
                if (findChannel != null) {
                    //用户在线
                    users.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
                }
            }
        }
        //如果是签收类型的消息，针对具体的消息进行签收，同时修改签收状态为【已签收】
        else if (MsgActionEnum.SIGNED.type == action) {
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            //扩展字段在signed类型消息中，代表要去签收的消息id,逗号间隔
            String msgIdsStr = dataContent.getExtend();
            String[] msgIds = msgIdsStr.split(",");

            List<String> msgIdsList = new ArrayList<>();
            for (String msgId : msgIds) {
                if (StringUtils.isNoneBlank(msgId))
                    msgIdsList.add(msgId);
            }

            if (!msgIdsList.isEmpty()) {
                //批量签收
                userService.updateMsgSigned(msgIdsList);
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        //发生异常要关闭连接，同时从channel group中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
