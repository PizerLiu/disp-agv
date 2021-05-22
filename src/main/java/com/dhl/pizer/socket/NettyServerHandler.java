package com.dhl.pizer.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.entity.Set;
import com.dhl.pizer.service.SetService;
import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.util.SpringContextUtil;
import com.dhl.pizer.vo.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private TaskService taskService;

    /**
     * 传感器触发标志位
     */
    private static boolean tag = true;

    /**
     * 货物取走标志位
     */
    private static boolean hTag = true;

    /**
     * 启动亮灯标志
     */
    private static boolean initLedTag = true;

    /**
     * 记录regId -> 灯
     * true 亮
     * false 灭
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

    public void sethTag(boolean hTag) {
        this.hTag = hTag;
    }

    /**
     * 给对应点发送消息
     */
    public boolean sendMessage(String regId, String message) {
        ChannelHandlerContext ctx = regIdCtx.get(regId);
        if (ctx == null) {
            return false;
        }

        ctx.writeAndFlush(message);
        return true;
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

        JSONObject messageJson = null;
        try {
            messageJson = JSONObject.parseObject(msg.toString());
        } catch (Exception e) {
            return;
        }
        Message message = JSON.toJavaObject(messageJson, Message.class);

        if (message == null ||
                message.getRegId() == null || message.getRegId().isEmpty() ||
                message.getIoNum() == null || message.getIoNum().isEmpty() ||
                message.getIoState() == null || message.getIoState().isEmpty()) {
            return;
        }

        // 存储client的ctx
        regIdCtx.put(message.getRegId(), ctx);
        if (message.getRegId().equals("LOC-AP5") && initLedTag) {
            // 开绿灯
            initLedTag = false;
            ctx.write("OPEN 1");
        }

        // 存储每个设备的绿灯状态
        if (message.getRegId().equals("LOC-AP1")) {
            regIdGreenLed.put("LOC-AP11", message.getIoState().substring(4, 6).equals("01"));
        } else {
            regIdGreenLed.put(message.getRegId(), message.getIoState().substring(1, 2).equals("0"));
        }

        // 取货口，有货物到了
        taskService = SpringContextUtil.getApplicationContext().getBean(TaskService.class);
        Set set = taskService.setting("601764207ab6bd57abbe0af0");
        if (set == null || !set.isSetpower()) {
            return;
        }

        Set hTagSet = taskService.setting("601764207ab6bd57abbe0af1");
        if (hTagSet == null) {
            SetService setService = SpringContextUtil.getApplicationContext().getBean(SetService.class);
            Set set1 = new Set();
            set1.setId("601764207ab6bd57abbe0af1");
            set1.setHTag(true);
            set1.setUpdateTime(new Date());
            setService.addOrUpdate(set1);
        }
        hTag = hTagSet.isHTag();

        // todo LOC-AP1 才是16位的，其他为8位；
//        log.info("tag = " + tag);
//        log.info("hTag = " + hTag);
        if (message.getRegId().equals("LOC-AP1") && message.getIoState().substring(11, 12).equals("0") && hTag) {

            SetService setService = SpringContextUtil.getApplicationContext().getBean(SetService.class);

            // 数据库开关
            Set currentf0Set = setService.findAllById("601764207ab6bd57abbe0af0");
            if (!currentf0Set.isSetPhotoelectricity()) {
                log.warn("请检查数据库配置：601764207ab6bd57abbe0af0， 请将setPhotoelectricity设置为true！");
                return;
            }

            // 30s内触发的只当作一次
            Set currentSet = setService.findAllById("601764207ab6bd57abbe0af1");
            if ( new Date().getTime() - currentSet.getUpdateTime().getTime() < 30 * 1000 ) {
                log.warn("光电30s内无法同时触发！");
                return;
            }

            log.info("成功触发任务！");

            currentSet.setHTag(false);
            setService.addOrUpdate(currentSet);

            taskService.createTask("阿斯利康-传感器触发", "LOC-AP1", "");
        } else if (message.getRegId().equals("LOC-AP1") && message.getIoState().substring(11, 12).equals("1") && !tag && hTag) {
//            log.info("打开触发限制");
//
//            SetService setService = SpringContextUtil.getApplicationContext().getBean(SetService.class);
//
//            Set set2 = new Set();
//            set2.setId("601764207ab6bd57abbe0af1");
//            set2.setHTag(true);
//            set2.setUpdateTime(new Date());
//            setService.addOrUpdate(set2);
        } else {
            // 只有8位的数据
            // 保存其他灯的红绿情况
            // 判断放货点数据，是否有货，更新库位的lock, 0为加锁，1为解锁
            taskService.setLocationLock(message.getRegId(), message.getIoState().substring(1, 2).equals("0"));
        }
//        log.info("服务器收到消息: {}", message.toString());
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