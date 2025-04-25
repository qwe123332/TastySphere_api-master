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

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Send a message to another user
     */
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

    /**
     * Get conversation with a specific user (paginated)
     */
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

    /**
     * Get total unread message count for current user
     */
    @GetMapping("/unread")
    public ResponseEntity<Integer> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails user) {
        int count = messageService.getUnreadCount(user.getUser().getId());
        return ResponseEntity.ok(count);
    }

    /**
     * Delete entire conversation with a specific user
     */
    @DeleteMapping("/conversations/{partnerId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long partnerId,
            @AuthenticationPrincipal CustomUserDetails user) {
        messageService.deleteConversation(user.getUser().getId(), partnerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark a specific message as read
     */
    @PostMapping("/read/{messageId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long messageId,
            @AuthenticationPrincipal CustomUserDetails user) {
        messageService.markAsRead(messageId, user.getUser().getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Get list of conversation previews for the chat list UI
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationPreview>> getConversations(
            @AuthenticationPrincipal CustomUserDetails user) {
        List<ConversationPreview> result = messageService.getConversationPreviews(user.getUser().getId());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnread_Count(
            @AuthenticationPrincipal CustomUserDetails user) {
        int count = messageService.getUnreadCount(user.getUser().getId());
        return ResponseEntity.ok(count);
    }
}