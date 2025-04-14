package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.dto.ConversationPreview;
import com.example.tastysphere_api.entity.PrivateMessage;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.PrivateMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private PrivateMessageMapper messageMapper;
    @Autowired
    private UserService userService;

    public void sendMessage(Long senderId, Long receiverId, String content) {
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setRead(false);
        message.setTimestamp(LocalDateTime.now());

        messageMapper.insert(message);
    }

    public List<PrivateMessage> getConversation(Long userId1, Long userId2, int page, int size) {
        int offset = page * size;
        return messageMapper.getConversation(userId1, userId2, offset, size);
    }

    public Integer getUnreadCount(Long userId) {
        return messageMapper.countUnreadMessages(userId);
    }

    public void markAsRead(Long messageId, Long userId) {
        messageMapper.markAsRead(messageId, userId);
    }


    public List<Map<String, Object>> getConversations(Long userId) {
        List<Long> partnerIds = messageMapper.findConversationPartners(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            User partner = userService.getUserById(partnerId); // 获取用户名和头像
            PrivateMessage lastMsg = messageMapper.findLastMessageBetween(userId, partnerId);
            int unread = messageMapper.countUnreadFromUser(partnerId, userId);

            Map<String, Object> map = new HashMap<>();
            map.put("userId", partner.getId());
            map.put("username", partner.getUsername());
            map.put("avatar", partner.getAvatar());
            map.put("lastMessage", lastMsg != null ? lastMsg.getContent() : "");
            map.put("lastTime", lastMsg != null ? lastMsg.getTimestamp() : null);
            map.put("unreadCount", unread);
            result.add(map);
        }

        // 按时间倒序排序
        result.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("lastTime");
            LocalDateTime timeB = (LocalDateTime) b.get("lastTime");
            return timeB.compareTo(timeA);
        });

        return result;
    }

    public void deleteConversation(Long userId, Long partnerId) {
        // 标记逻辑删除（假设使用字段 deleted_by_user_id）或直接删除自己的记录视图
        // 简化实现：彻底删除与该用户之间所有消息（不推荐正式环境用）
        QueryWrapper<PrivateMessage> wrapper = new QueryWrapper<>();
        wrapper.and(qw -> qw
                .eq("sender_id", userId).eq("receiver_id", partnerId)
                .or()
                .eq("sender_id", partnerId).eq("receiver_id", userId)
        );
        messageMapper.delete(wrapper);
    }

    public List<ConversationPreview> getConversationPreviews(Long userId) {
        List<ConversationPreview> previews = new ArrayList<>();
        List<Long> partnerIds = messageMapper.findConversationPartners(userId);

        for (Long partnerId : partnerIds) {
            User partner = userService.getUserById(partnerId);
            PrivateMessage lastMsg = messageMapper.findLastMessageBetween(userId, partnerId);
            int unreadCount = messageMapper.countUnreadFromUser(partnerId, userId);

            ConversationPreview preview = new ConversationPreview();
            preview.setUserId(partner.getId());
            preview.setUsername(partner.getUsername());
            preview.setAvatar(partner.getAvatar());
            preview.setLastMessage(lastMsg != null ? lastMsg.getContent() : "");
            preview.setLastTime(lastMsg != null ? lastMsg.getTimestamp() : null);
            preview.setUnreadCount(unreadCount);

            previews.add(preview);
        }

        return previews;
    }
}
