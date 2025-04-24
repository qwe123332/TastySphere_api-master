package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.dto.ConversationPreview;
import com.example.tastysphere_api.entity.Message;
import com.example.tastysphere_api.entity.PrivateMessage;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.MessageMapper;
import com.example.tastysphere_api.mapper.PrivateMessageMapper;
import com.example.tastysphere_api.util.UrlUtils;
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
    private PrivateMessageMapper privateMessageMapper;
    @Autowired
    private UserService userService;
    private final MessageMapper messageMapper;

    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public void saveMessage(Message message) {
        messageMapper.insertMessage(message);
    }

    public void sendMessage(Long senderId, Long receiverId, String content) {
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setRead(false);
        message.setTimestamp(LocalDateTime.now());

        privateMessageMapper.insert(message);
    }

    public List<PrivateMessage> getConversation(Long userId1, Long userId2, int page, int size) {
        int offset = page * size;
        return privateMessageMapper.getConversation(userId1, userId2, offset, size);
    }

    public Integer getUnreadCount(Long userId) {
        return privateMessageMapper.countUnreadMessages(userId);
    }

    public void markAsRead(Long messageId, Long userId) {
        privateMessageMapper.markAsRead(messageId, userId);
    }


    public List<Map<String, Object>> getConversations(Long userId) {
        List<Long> partnerIds = privateMessageMapper.findConversationPartners(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            User partner = userService.getUserById(partnerId); // 获取用户名和头像
            PrivateMessage lastMsg = privateMessageMapper.findLastMessageBetween(userId, partnerId);
            int unread = privateMessageMapper.countUnreadFromUser(partnerId, userId);

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
        privateMessageMapper.delete(wrapper);
    }

    public List<ConversationPreview> getConversationPreviews(Long userId) {
        List<ConversationPreview> previews = new ArrayList<>();
        List<Long> partnerIds = privateMessageMapper.findConversationPartners(userId);

        for (Long partnerId : partnerIds) {
            User partner = userService.getUserById(partnerId);
            PrivateMessage lastMsg = privateMessageMapper.findLastMessageBetween(userId, partnerId);
            int unreadCount = privateMessageMapper.countUnreadFromUser(partnerId, userId);

            ConversationPreview preview = new ConversationPreview();
            preview.setUserId(partner.getId());
            preview.setUsername(partner.getUsername());
            preview.setAvatar(UrlUtils.resolveAvatarUrl(partner.getAvatar()));
            preview.setLastMessage(lastMsg != null ? lastMsg.getContent() : "");
            preview.setLastTime(lastMsg != null ? lastMsg.getTimestamp() : null);
            preview.setUnreadCount(unreadCount);

            previews.add(preview);
        }

        return previews;
    }
}
