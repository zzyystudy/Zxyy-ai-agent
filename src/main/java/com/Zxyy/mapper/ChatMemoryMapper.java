package com.Zxyy.mapper;

import com.Zxyy.entity.ChatMemoryEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ChatMemoryMapper {

    @Insert("INSERT INTO chat_memory(conversation_id, message_type, content, metadata, created_at) " +
            "VALUES(#{conversationId}, #{messageType}, #{content}, #{metadata}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMemoryEntity entity);

    @Select("SELECT * FROM chat_memory WHERE conversation_id = #{conversationId} ORDER BY created_at ASC")
    List<ChatMemoryEntity> selectByConversationId(String conversationId);

    @Delete("DELETE FROM chat_memory WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(String conversationId);

    @Delete("DELETE FROM chat_memory WHERE conversation_id = #{conversationId} " +
            "AND id NOT IN (" +
            "  SELECT id FROM (" +
            "    SELECT id FROM chat_memory " +
            "    WHERE conversation_id = #{conversationId} " +
            "    ORDER BY created_at DESC " +
            "    LIMIT #{maxMessages}" +
            "  ) tmp" +
            ")")
    int deleteOldMessages(@Param("conversationId") String conversationId, @Param("maxMessages") int maxMessages);
}