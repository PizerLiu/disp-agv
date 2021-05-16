package com.dhl.pizer.socket;

import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.service.RegService;
import com.dhl.pizer.util.SpringContextUtil;
import com.dhl.pizer.vo.DiResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private RegService regService;

    /**
     * 相机触发标志位
     */
    private static boolean camTag = false;
    
    private JSONObject messageJson;

    public boolean isCamTag() {
        return camTag;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端Active .....");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        String msg1 = msg.toString().substring(0, msg.toString().indexOf("{"));
        String msg2 = msg.toString().substring(msg.toString().indexOf("{"));
        messageJson = JSONObject.parseObject(msg2);
//        log.info("客户端收到消息:{}", msg2);

        regService = SpringContextUtil.getApplicationContext().getBean(RegService.class);
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}