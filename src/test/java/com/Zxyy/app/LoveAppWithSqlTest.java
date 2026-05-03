package com.Zxyy.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppWithSqlTest {

    @Resource
    public LoveAppWithSql loveAppWithSql;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好，我是Zxyy宇";
        String answer = loveAppWithSql.doChat(message, chatId);
        //第二轮
        message = "我刚谈了一个女朋友 她叫散尽，我想送她100天礼物，该送什么好？";
        answer = loveAppWithSql.doChat(message, chatId);
        //第三轮
        message = "我和我的另一半叫什么？";
        answer = loveAppWithSql.doChat(message, chatId);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我现在单身怎么办";
        String answer = loveAppWithSql.doChatWithRag(message, chatId);
    }
}