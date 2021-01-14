package com.gee.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        //获取管道
        ChannelPipeline pipeline = channel.pipeline();
        //添加websocket基于http协议，所需要的http编解码器
        pipeline.addLast(new HttpServerCodec());
        //添加对数据流写支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对httpMessage进行聚合处理，聚合为request或response
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
        //处理http握手动作
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //自定义handler
        pipeline.addLast(new ChatHandler());
    }
}
