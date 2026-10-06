package start.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 * id 规则：
 *   顾客端：user_{userId}     （如 user_1）
 *   商家端：admin_{merchantId} （如 admin_2）
 *
 * 消息格式（精准推送，目标ID写在消息体中）：
 *   顾客催单：reminder:{merchantId}   如 "reminder:2" 表示催单给商家2
 *   商家回复：received:{userId}       如 "received:1" 表示回复给用户1
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{id}")
public class WebSocketServer {

    /** 顾客端会话：key = userId（纯数字字符串，如 "1"） */
    private static final Map<String, Session> userSessions = new HashMap<>();

    /** 商家端会话：key = merchantId（纯数字字符串，如 "2"） */
    private static final Map<String, Session> adminSessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        log.info("客户端建立连接：{}", id);
        if (id.startsWith("admin_")) {
            adminSessions.put(id.substring("admin_".length()), session);
        } else if (id.startsWith("user_")) {
            userSessions.put(id.substring("user_".length()), session);
        }
    }

    @OnClose
    public void onClose(@PathParam("id") String id) {
        log.info("连接断开：{}", id);
        if (id.startsWith("admin_")) {
            adminSessions.remove(id.substring("admin_".length()));
        } else if (id.startsWith("user_")) {
            userSessions.remove(id.substring("user_".length()));
        }
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") String id) {
        log.info("收到来自 {} 的消息：{}", id, message);

        // 顾客催单：reminder:{merchantId} → 推给指定商家
        if (message.startsWith("reminder:") && id.startsWith("user_")) {
            String merchantId = message.substring("reminder:".length());
            sendToSession(adminSessions.get(merchantId), "reminder");
        }

        // 商家回复：received:{userId} → 推给指定顾客
        if (message.startsWith("received:") && id.startsWith("admin_")) {
            String userId = message.substring("received:".length());
            sendToSession(userSessions.get(userId), "received");
        }
    }

    /** 向指定 Session 发送消息 */
    private static void sendToSession(Session session, String message) {
        if (session == null) {
            return;
        }
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("WebSocket 发送消息失败", e);
        }
    }

    /**
     * 向指定用户推送消息（供外部调用，如定时任务）
     *
     * @param userId  用户 id
     * @param message 消息内容
     */
    public static void sendToUser(String userId, String message) {
        sendToSession(userSessions.get(userId), message);
    }

    /**
     * 向指定商家推送消息（供外部调用，如定时任务）
     *
     * @param merchantId 商家 id
     * @param message    消息内容
     */
    public static void sendToAdmin(String merchantId, String message) {
        sendToSession(adminSessions.get(merchantId), message);
    }
}
