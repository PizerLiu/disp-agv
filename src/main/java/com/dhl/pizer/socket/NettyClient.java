package com.dhl.pizer.socket;

import com.dhl.pizer.util.ConvertCode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();

    private Integer port = 19204;
    
    private String host = "192.168.1.223";

    private SocketChannel socketChannel;

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param hexString 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return bytes;

    }

    /**
     * 发送消息
     */
    public void sendMsg(String msg) {

        //        int[] send = {0x5A, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x03, 0xF5, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

//        System.out.println(hexStringToByteArray("5A 01 00 01 00 00 00 00 03 F5 00 00 00 00 00 00"));

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(ConvertCode.hexString2Bytes(msg));

        socketChannel.writeAndFlush(byteBuf);
    }

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyClientInitializer());
        ChannelFuture future = bootstrap.connect();
        //客户端断线重连逻辑
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                log.info("连接Netty服务端成功");
            } else {
                log.info("连接失败，进行断线重连");
                future1.channel().eventLoop().schedule(() -> start(), 20, TimeUnit.SECONDS);
            }
        });
        socketChannel = (SocketChannel) future.channel();
    }

    public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast("decoder", new StringDecoder());
            socketChannel.pipeline().addLast("encoder", new StringEncoder());
            socketChannel.pipeline().addLast(new NettyClientHandler());
        }
    }
}
