package com.Zxyy.Memory;


import com.Zxyy.entity.ChatMemoryEntity;
import com.Zxyy.mapper.ChatMemoryMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实现chatmemory接口
 * 在mysql中存储对话记录
 */
@Slf4j
@Component
public class MyMemoryInMysql implements ChatMemory {

    private final ChatMemoryMapper chatMemoryMapper;
    private final ObjectMapper objectMapper;
    private final int maxMessages;

    public MyMemoryInMysql(ChatMemoryMapper chatMemoryMapper, ObjectMapper objectMapper) {
        this.chatMemoryMapper = chatMemoryMapper;
        this.objectMapper = objectMapper;
        this.maxMessages = 10;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (Message message : messages) {
            ChatMemoryEntity entity = new ChatMemoryEntity();
            entity.setConversationId(conversationId);
            entity.setMessageType(message.getMessageType().name());
            entity.setContent(message.getText());

            try {
                Map<String, Object> metadata = new HashMap<>();
                metadata.putAll(message.getMetadata());
                entity.setMetadata(objectMapper.writeValueAsString(metadata));
            } catch (JsonProcessingException e) {
                log.warn("序列化消息元数据失败", e);
                entity.setMetadata("{}");
            }

            chatMemoryMapper.insert(entity);
        }

        // 清理旧消息，保留最近的 maxMessages 条
        cleanupOldMessages(conversationId);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<ChatMemoryEntity> entities = chatMemoryMapper.selectByConversationId(conversationId);

        return entities.stream()
                .map(this::convertToMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        chatMemoryMapper.deleteByConversationId(conversationId);
        log.info("清空会话记忆: {}", conversationId);
    }

    private Message convertToMessage(ChatMemoryEntity entity) {
        MessageType messageType = MessageType.valueOf(entity.getMessageType());

        switch (messageType) {
            case USER:
                return new UserMessage(entity.getContent());
            case ASSISTANT:
                return new AssistantMessage(entity.getContent());
            case SYSTEM:
                return new SystemMessage(entity.getContent());
            default:
                log.warn("未知的消息类型: {}", messageType);
                return new UserMessage(entity.getContent());
        }
    }

    private void cleanupOldMessages(String conversationId) {
        List<ChatMemoryEntity> entities = chatMemoryMapper.selectByConversationId(conversationId);

        if (entities.size() > maxMessages) {
            int toDelete = entities.size() - maxMessages;
            List<Long> idsToDelete = entities.subList(0, toDelete)
                    .stream()
                    .map(ChatMemoryEntity::getId)
                    .collect(Collectors.toList());

            if (!idsToDelete.isEmpty()) {
                for (Long id : idsToDelete) {
                    chatMemoryMapper.deleteByConversationId(conversationId);
                }
                log.debug("清理旧消息，会话: {}, 删除数量: {}", conversationId, toDelete);
            }
        }
    }
}
