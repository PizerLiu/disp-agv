package com.dhl.pizer.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.vo.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private TaskService taskService;

    /**
     * 记录regId -> 灯
     */
    private static Map<String, Boolean> regIdGreenLed = new ConcurrentHashMap();

    /**
     * 缓存客户端
     */
    private static Map<String, ChannelHandlerContext> regIdCtx = new ConcurrentHashMap<>();

    /**
     * 获取regId对应客户端
     */
    public static ChannelHandlerContext getCtx(String regId) {
        return regIdCtx.get(regId);
    }

    /**
     * 获取regId对应放货库位绿灯状态
     */
    public boolean checkGreenLedStatus(String regId) {
        return regIdGreenLed.get(regId);
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

        // 取货口，有货物到了
        if (message.getRegId().equals("NO.1") && message.getIoState().endsWith("11")) {
            taskService.createTask("阿斯利康-传感器触发", "AP1002");
        }

        regIdCtx.put(message.getRegId(), ctx);
        regIdGreenLed.put(message.getRegId(), message.getIoState().substring(1, 2).equals("1"));

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
