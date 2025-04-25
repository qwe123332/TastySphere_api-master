package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.dto.ConversationPreview;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.entity.Message;
import com.example.tastysphere_api.entity.PrivateMessage;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.PrivateMessageMapper;
import com.example.tastysphere_api.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final PrivateMessageMapper privateMessageMapper;
    private final UserService userService;

    @Autowired
    public MessageService(PrivateMessageMapper privateMessageMapper, UserService userService) {
        this.privateMessageMapper = privateMessageMapper;
        this.userService = userService;
    }

    /**
     * Legacy method for backward compatibility
     * Converts and saves a message from the old format
     */
    public void saveMessage(Message message) {
        // Convert from legacy Message to PrivateMessage
        PrivateMessage privateMessage = new PrivateMessage();

        // Handle different ID formats (String vs Long)
        privateMessage.setSenderId(Long.valueOf(message.getFromUserId()));
        privateMessage.setReceiverId(Long.valueOf(message.getToUserId()));
        privateMessage.setContent(message.getContent());
        privateMessage.setRead(false); // Default to unread

        // Use the message's timestamp if available, otherwise use current time
        privateMessage.setTimestamp(message.getCreatedTime() != null ?
                message.getCreatedTime() :
                LocalDateTime.now());

        // Save using the mapper
        privateMessageMapper.insert(privateMessage);

        // Log the save operation with current date and user login
        log.info("Message saved at {} by user {}: from {} to {}",
                LocalDateTime.now(),
                "qwe123332", // Current user login from your input
                message.getFromUserId(),
                message.getToUserId());
    }

    public void sendMessage(Long senderId, Long receiverId, String content) {
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setRead(false);
        message.setTimestamp(LocalDateTime.now());

        privateMessageMapper.insert(message);
        log.info("Message sent at {} by userId {}: to {}", LocalDateTime.now(), senderId, receiverId);
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
        log.info("Message {} marked as read by user {}", messageId, userId);
    }

    public void deleteConversation(Long userId, Long partnerId) {
        privateMessageMapper.deleteConversation(userId, partnerId);
        log.info("Conversation deleted at {} between users {} and {}",
                LocalDateTime.now(), userId, partnerId);
    }

    public List<ConversationPreview> getConversationPreviews(Long userId) {
        // Use the optimized SQL query from the mapper
        List<ConversationPreview> conversationPreviews = privateMessageMapper.getConversationPreviews(userId);
        for (ConversationPreview preview : conversationPreviews) {
            // Fetch user details for each conversation partner
            preview.setAvatar(UrlUtils.resolveAvatarUrl(preview.getAvatar()));
        }
        return conversationPreviews;
    }

    public String getUserIdByEmail(String input) {
        // Check if the input is a valid email

            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", input);
            User user = userService.getOne(queryWrapper);
            return user != null ? String.valueOf(user.getId()) : null;

        // If not, assume it's a user ID
    }


    public void markConversationAsRead(Long receiverId, Long senderId) {
        privateMessageMapper.markConversationAsRead(receiverId, senderId);
        log.info("Conversation marked as read: receiver={}, sender={}", receiverId, senderId);
    }

}