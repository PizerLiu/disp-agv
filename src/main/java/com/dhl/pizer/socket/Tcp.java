package com.dhl.pizer.socket;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//@Component
public class Tcp {

    private static int SrdStatePort = 19204;

    private Socket client;

    @PostConstruct
    public void init() {
        try {
            client = new Socket("127.0.0.1", SrdStatePort);
            System.out.println("客户端建立成功！");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String checkCameraSig(byte[] req) {

        String data = null;

        try {
            OutputStream pt = client.getOutputStream();
            pt.write(req);

            InputStream input = client.getInputStream();
            byte[] b = new byte[1024];

            int len = input.read(b);
            data = new String(b, 0, len);
            System.out.println("收到服务器消息：" + data);

            client.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

}
