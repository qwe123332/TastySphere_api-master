package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.ConversationPreview;
import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.request.MessageRequest;
import com.example.tastysphere_api.entity.PrivateMessage;
import com.example.tastysphere_api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 发送消息
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        messageService.sendMessage(
                user.getUser().getId(),
                request.getReceiverId(),
                request.getContent()
        );
        return ResponseEntity.ok().build();
    }


    // 获取与指定用户的对话内容（分页）
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<PrivateMessage>> getConversation(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails user) {
        List<PrivateMessage> messages = messageService.getConversation(
                user.getUser().getId(), userId, page, size
        );
        return ResponseEntity.ok(messages);
    }

    // 获取当前用户的未读消息数
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails user) {
        int count = messageService.getUnreadCount(user.getUser().getId());
        return ResponseEntity.ok(count);
    }
    @DeleteMapping("/conversations/{partnerId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long partnerId,
            @AuthenticationPrincipal CustomUserDetails user) {
        messageService.deleteConversation(user.getUser().getId(), partnerId);
        return ResponseEntity.ok().build();
    }

    // 标记某条消息为已读
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long messageId,
            @AuthenticationPrincipal CustomUserDetails user) {
        messageService.markAsRead(messageId, user.getUser().getId());
        return ResponseEntity.ok().build();
    }
    // 聊天联系人列表（用于聊天列表页)
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationPreview>> getConversations(
            @AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUser().getId();
        List<ConversationPreview> result = messageService.getConversationPreviews(userId);
        return ResponseEntity.ok(result);
    }

}
