package com.dhl.pizer.socket;

import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

//judge/photoelectricity
/**
 * @ServerEndpoint(value = "/") 光电客户端接收信息请求
 */
@Slf4j
@Component
@ServerEndpoint(value = "/")
public class NoticeWebSocket {

    @Autowired
    private TaskService taskService;

    /**
     * 记录当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 记录ip -> 灯
     */
    private static HashMap<String, String> ipLed = new HashMap();

    /**
     * 记录灯 -> session
     */
    private static HashMap<String, String> ledSession = new HashMap();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        onlineCount.incrementAndGet(); // 在线数加1
        log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet(); // 在线数减1
        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

        System.out.println("SO_LINGER = " + message);

//        // 更新灯
//        ipLed.put("", "");
//
//        taskService = SpringContextUtil.getApplicationContext().getBean(TaskService.class);
//
//        log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);
//
//        if (message.equals("123")) {
//            // 无指定开始位置
//            taskService.createTask("阿斯利康-传感器触发", "");
//        }
//
//        this.sendMessage("Hello, " + message, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    public String checkLedStatus(String ip) {
        return ipLed.get(ip);
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：{}", e);
        }
    }
}
