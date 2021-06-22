package com.dhl.pizer.socket;

import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.service.RegService;
import com.dhl.pizer.util.SpringContextUtil;
import com.dhl.pizer.vo.DiResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private RegService regService;

    @Autowired
    private NoticeWebSocket noticeWebSocket;

    /**
     * 相机触发标志位
     */
    private static boolean camTag = false;

    /**
     * AGV房钱电量
     */
    private static int batteryVoltage = 0;
    
    private JSONObject messageJson;

    public boolean isCamTag() {
        return camTag;
    }

    public static int getBatteryVoltage() {
        return batteryVoltage;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端Active .....");
    }

    public static void main(String[] args) {
        String ss = "123123{asdasdsad\"DO\":";
        System.out.println(ss.substring(0, ss.indexOf("\"DO\":")));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!msg.toString().contains("DI")) {
            if (!msg.toString().contains("battery_temp")) {
                return;
            }
        }

//        log.info("client message1: {}", msg);
        String msg2 = msg.toString().substring(msg.toString().indexOf("{"));

        if (msg2.contains(",\"DO\"")) {
            msg2 = msg2.substring(0, msg2.indexOf(",\"DO\"")) + "}";
        }

//        log.info("client message2: {}", msg2);
        messageJson = JSONObject.parseObject(msg2);

        regService = SpringContextUtil.getApplicationContext().getBean(RegService.class);
        noticeWebSocket = SpringContextUtil.getApplicationContext().getBean(NoticeWebSocket.class);

        // 接收电量数据
        if (messageJson.containsKey("battery_temp") && messageJson.containsKey("voltage")) {
            batteryVoltage = Integer.valueOf(messageJson.get("voltage").toString());

            if (batteryVoltage < 30) {
                noticeWebSocket.sendMessage("电量低于30%了，请检查；");
            }

        }

        // 接收DI信号
        if (messageJson.containsKey("DI")) {
            List<DiResult> dis = JSONObject.parseArray(messageJson.get("DI").toString(), DiResult.class);
            for (DiResult di : dis) {
                // 相机di检测
                if (di.getId() == 6) {
                    camTag = di.isStatus();
                }

//            // 关闭安全区域，允许过人
//            if (di.getId() == 9 && di.isStatus()) {
//                // 绿灯
//                regService.setRegLed("LOC-AP1", true);
//            }
//
//            // 打开安全区域，禁止过人
//            if (di.getId() == 10 && di.isStatus()) {
//                // 红灯
//                regService.setRegLed("LOC-AP1", false);
//            }
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}