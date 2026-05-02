package com.Zxyy.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ChatMemoryEntity {
    private Long id;
    private String conversationId;
    private String messageType;
    private String content;
    private String metadata;

    private LocalDateTime createdAt;
}