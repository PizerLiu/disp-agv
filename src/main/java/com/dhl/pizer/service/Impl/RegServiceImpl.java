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
            ctx.write("OPEN 1");
        } else {
            ctx.write("CLOSE 1");
        }

        ctx.flush();
    }

}
