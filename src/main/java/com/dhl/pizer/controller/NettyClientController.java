package com.dhl.pizer.controller;

import com.dhl.pizer.socket.NettyClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NettyClientController {
    @Autowired
    private NettyClient nettyClient;

    @GetMapping("/send")
    public String send() {
        nettyClient.sendMsg("5A 01 00 01 00 00 00 00 03 F5 00 00 00 00 00 00");
        return "发送成功！";
    }
}
