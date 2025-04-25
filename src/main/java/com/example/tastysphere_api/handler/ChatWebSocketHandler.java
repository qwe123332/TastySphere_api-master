package com.example.tastysphere_api.handler;

import com.alibaba.fastjson2.JSONObject;
import com.example.tastysphere_api.entity.Message;
import com.example.tastysphere_api.security.JwtUtils;
import com.example.tastysphere_api.service.MessageService;
import com.example.tastysphere_api.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final MessageService messageService;
    private final JwtUtils jwtUtils;
    private  final UserService userService;

    public ChatWebSocketHandler(MessageService messageService, JwtUtils jwtUtils, UserService userService) {
        this.messageService = messageService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String token = getParam(session, "token");
        if (token != null) {
            String email = jwtUtils.extractUserId(token);
            String userId = userService.getUserIdByEmail(email); // 假设你有一个方法可以通过 email 获取 userId
            if (userId != null) {
                sessions.put(userId, session);
                System.out.println("WebSocket连接成功: " + userId);
                try {
                    JSONObject msg = new JSONObject();
                    msg.put("type", "SYSTEM");
                    msg.put("content", "CONNECTED");
                    session.sendMessage(new TextMessage(msg.toJSONString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("JWT解析失败: 无法识别userId");
            }
        } else {
            System.out.println("WebSocket连接失败: 未提供token参数");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        JSONObject msg = JSONObject.parseObject(textMessage.getPayload());
        String type = msg.getString("type");

        // 🔁 处理心跳 PING 消息
        if ("PING".equals(type)) {
            JSONObject pong = new JSONObject();
            pong.put("type", "PONG");
            pong.put("timestamp", System.currentTimeMillis()); // 可选：回传时间戳
            session.sendMessage(new TextMessage(pong.toJSONString()));
            System.out.println("收到PING，已响应PONG");
            return;
        }
        if ("MARK_READ".equals(type)) {
            String token = getParam(session, "token");
            String email = jwtUtils.extractUserId(token);
            Long receiverId = Long.valueOf(userService.getUserIdByEmail(email)); // 当前登录用户

            String  senderRaw=msg.getString("to");
            Long senderId = Long.valueOf(resolveUserId(senderRaw)); // 对方

            if (receiverId != null && senderId != null) {
                messageService.markConversationAsRead(receiverId, senderId);
                System.out.println("WebSocket: 已将对话标记为已读 -> from: " + senderId + " to: " + receiverId);
            } else {
                System.out.println("MARK_READ 参数解析失败");
            }
            return;
        }


        // 认证和消息处理逻辑
        String token = getParam(session, "token");
        String email = jwtUtils.extractUserId(token);
        String from = userService.getUserIdByEmail(email);
        String toRaw = msg.getString("to");
        String to = resolveUserId(toRaw);

        String content = msg.getString("content");

        if (from == null || to == null || content == null) {
            System.out.println("非法消息格式，from/to/content不能为空");
            return;
        }

        Message message = new Message(null, from, to, content, LocalDateTime.now());
        messageService.saveMessage(message);

        WebSocketSession toSession = sessions.get(to);
        if (toSession != null && toSession.isOpen()) {
            JSONObject payload = new JSONObject();
            payload.put("type", "CHAT");
            payload.put("from", from);
            payload.put("content", content);
            payload.put("timestamp", message.getCreatedTime().toString());
            toSession.sendMessage(new TextMessage(payload.toJSONString()));
        }

        if (session.isOpen()) {
            JSONObject ack = new JSONObject();
            ack.put("type", "ACK");
            ack.put("status", "SENT");
            ack.put("timestamp", message.getCreatedTime().toString());
            session.sendMessage(new TextMessage(ack.toJSONString()));
        }
    }

    private String getParam(WebSocketSession session, String key) {
        String query = session.getUri().getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return null;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().removeIf(ws -> ws.equals(session));
    }


    private String resolveUserId(String input) {
        // 如果已经是数字 ID，直接返回
        if (input != null && input.matches("\\d+")) {
            return input;
        }

        // 如果是邮箱，去数据库查找对应的用户 ID
        if (input != null && input.contains("@")) {
            try {
                String userId = messageService.getUserIdByEmail(input); // 你需要在 MessageService 或 UserService 中添加此方法
                if (userId != null) {
                    return userId;
                } else {
                    throw new IllegalArgumentException("找不到邮箱对应的用户: " + input);
                }
            } catch (Exception e) {
                System.err.println("用户ID解析失败: " + e.getMessage());
            }
        }

        return null;
    }

}
