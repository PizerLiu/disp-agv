package com.dhl.pizer.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.vo.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //本地缓存
    private static Map<String, ChannelHandlerContext> concurrentHashMap = new ConcurrentHashMap<>();

    public static ChannelHandlerContext getCtx(String regId) {
        return concurrentHashMap.get(regId);
    }

    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        log.info("客户端连接！clientIP = " + clientIP);
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        JSONObject messageJson = JSONObject.parseObject(msg.toString());
        Message message = JSON.toJavaObject(messageJson, Message.class);

        if (message == null ||
                message.getRegId() == null || message.getRegId().isEmpty() ||
                message.getIoNum() == null || message.getIoNum().isEmpty() ||
                message.getIoState() == null || message.getIoState().isEmpty()) {
            return;
        }

        concurrentHashMap.put(message.getRegId(), ctx);

        log.info("服务器收到消息: {}", message.toString());
        ctx.flush();
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
