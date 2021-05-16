package com.dhl.pizer.service.Impl;

import com.dhl.pizer.service.RegService;
import com.dhl.pizer.socket.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegServiceImpl implements RegService {

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Override
    public void setRegLed(String regId, boolean on) {

        ChannelHandlerContext ctx = nettyServerHandler.getCtx(regId);
        if (on) {
            // 开绿灯
            if (regId.equals("LOC-AP1")) {
                ctx.write("CLOSE 6");
                ctx.write("OPEN 5");
            } else {
                ctx.write("OPEN 1");
            }

        } else {
            // 开红灯
            if (regId.equals("LOC-AP1")) {
                ctx.write("CLOSE 5");
                ctx.write("OPEN 6");
            } else {
                ctx.write("CLOSE 1");
            }

        }

        ctx.flush();
    }

}
