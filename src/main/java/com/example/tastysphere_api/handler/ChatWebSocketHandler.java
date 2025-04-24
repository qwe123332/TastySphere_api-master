package com.example.tastysphere_api.handler;

import com.alibaba.fastjson2.JSONObject;

import com.example.tastysphere_api.entity.Message;
import com.example.tastysphere_api.service.MessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final MessageService messageService;

    public ChatWebSocketHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getParam(session, "userId");
        if (userId != null) {
            sessions.put(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        JSONObject msg = JSONObject.parseObject(textMessage.getPayload());
        String to = msg.getString("to");
        String from = getParam(session, "userId");
        String content = msg.getString("content");

        // 保存消息
        messageService.saveMessage(new Message(null, from, to, content));

        // 发送给接收者
        WebSocketSession toSession = sessions.get(to);
        if (toSession != null && toSession.isOpen()) {
            JSONObject payload = new JSONObject();
            payload.put("from", from);
            payload.put("content", content);
            toSession.sendMessage(new TextMessage(payload.toJSONString()));
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
}
