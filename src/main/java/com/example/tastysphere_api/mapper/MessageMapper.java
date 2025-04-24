package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Insert("INSERT INTO messages (from_user_id, to_user_id, content) VALUES (#{fromUserId}, #{toUserId}, #{content})")
    void insertMessage(Message message);

    // 其他方法...

}
