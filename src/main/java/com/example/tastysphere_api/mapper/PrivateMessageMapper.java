package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.dto.ConversationPreview;
import com.example.tastysphere_api.entity.PrivateMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {

    // 获取某两个用户之间的对话（分页）
    @Select("""
        SELECT * FROM private_message 
        WHERE (sender_id = #{userId1} AND receiver_id = #{userId2})
           OR (sender_id = #{userId2} AND receiver_id = #{userId1})
        ORDER BY timestamp DESC
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<PrivateMessage> getConversation(Long userId1, Long userId2, int offset, int limit);

    // 获取某个用户的未读消息数
    @Select("""
        SELECT COUNT(*) FROM private_message 
        WHERE receiver_id = #{receiverId} AND is_read = FALSE
    """)
    Integer countUnreadMessages(Long receiverId);

    // 标记某条消息为已读
    @Update("""
        UPDATE private_message 
        SET is_read = TRUE 
        WHERE id = #{messageId} AND receiver_id = #{receiverId}
    """)
    void markAsRead(Long messageId, Long receiverId);

    // 所有有过会话的用户 ID（发送或接收）
    @Select("""
    SELECT DISTINCT 
        CASE 
            WHEN sender_id = #{userId} THEN receiver_id 
            ELSE sender_id 
        END AS partner_id
    FROM private_message
    WHERE sender_id = #{userId} OR receiver_id = #{userId}
    """)
    List<Long> findConversationPartners(Long userId);

    // 最近一条消息
    @Select("""
    SELECT * FROM private_message
    WHERE 
      (sender_id = #{user1} AND receiver_id = #{user2})
      OR 
      (sender_id = #{user2} AND receiver_id = #{user1})
    ORDER BY timestamp DESC LIMIT 1
    """)
    PrivateMessage findLastMessageBetween(Long user1, Long user2);

    // 指定用户给当前用户的未读数
    @Select("""
    SELECT COUNT(*) FROM private_message
    WHERE sender_id = #{fromUser} AND receiver_id = #{toUser} AND is_read = FALSE
    """)
    int countUnreadFromUser(Long fromUser, Long toUser);

    // 删除两个用户之间的所有对话
    @Delete("""
    DELETE FROM private_message
    WHERE (sender_id = #{userId} AND receiver_id = #{partnerId})
       OR (sender_id = #{partnerId} AND receiver_id = #{userId})
    """)
    void deleteConversation(Long userId, Long partnerId);

    // 获取会话预览列表（带用户信息、最后消息和未读计数）
    @Select("""
    SELECT
        u.user_id AS userId,
        u.username,
        u.avatar,
        m.content AS lastMessage,
        m.timestamp AS lastTime,
        (
            SELECT COUNT(*) 
            FROM private_message pm
            WHERE pm.sender_id = u.user_id AND pm.receiver_id = #{currentUserId} AND pm.is_read = false
        ) AS unreadCount
    FROM (
        SELECT 
            CASE 
                WHEN sender_id = #{currentUserId} THEN receiver_id
                ELSE sender_id
            END AS user_id,
            MAX(timestamp) AS max_time
        FROM private_message
        WHERE sender_id = #{currentUserId} OR receiver_id = #{currentUserId}
        GROUP BY user_id
    ) recent
    JOIN private_message m ON (
        (m.sender_id = #{currentUserId} AND m.receiver_id = recent.user_id OR
         m.receiver_id = #{currentUserId} AND m.sender_id = recent.user_id)
        AND m.timestamp = recent.max_time
    )
    JOIN users u ON u.user_id = recent.user_id
    ORDER BY m.timestamp DESC
    """)
    List<ConversationPreview> getConversationPreviews(@Param("currentUserId") Long currentUserId);

    void markMessagesAsRead(Long fromId, Long toId);
    @Update("UPDATE private_message " +
            "SET is_read = 1 " +
            "WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    void markConversationAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
    @Select("SELECT COUNT(*) FROM private_message " +
            "WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    int countUnreadMessages(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

}