package com.dhl.pizer.controller;

import com.dhl.pizer.service.RegService;
import com.dhl.pizer.socket.NettyClient;

import com.dhl.pizer.socket.NettyClientHandler;
import com.dhl.pizer.socket.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NettyClientController {
    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private NettyClientHandler nettyClientHandler;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Autowired
    private RegService regService;

    @GetMapping("/send")
    public boolean send() {
//        nettyClient.sendMsg("5A 01 00 01 00 00 00 00 03 F5 00 00 00 00 00 00");
//        nettyClient.sendMsg("5A0100010000000003F5000000000000");

        regService.setRegLed("LOC-AP1", true);

        return true;
    }
}
