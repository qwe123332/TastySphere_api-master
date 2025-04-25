// --- MessageMapper.java ---
package com.example.tastysphere_api.mapper;

import com.example.tastysphere_api.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {
    @Insert("INSERT INTO messages (from_user_id, to_user_id, content, created_time) VALUES (#{fromUserId}, #{toUserId}, #{content}, #{createdTime})")
    void insertMessage(Message message);

    @Select("SELECT * FROM messages WHERE (from_user_id = #{user1} AND to_user_id = #{user2}) OR (from_user_id = #{user2} AND to_user_id = #{user1}) ORDER BY created_time ASC")
    List<Message> getConversation(String user1, String user2);
}
