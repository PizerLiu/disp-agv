package com.dhl.pizer.socket;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    
    private JSONObject messageJson;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端Active .....");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String msg1 = msg.toString().substring(0, msg.toString().indexOf("{"));
        String msg2 = msg.toString().substring(msg.toString().indexOf("{"));
        messageJson = JSONObject.parseObject(msg2);
        log.info("客户端收到消息:{}", msg2);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}