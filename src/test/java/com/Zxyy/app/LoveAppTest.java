package com.Zxyy.app;

import com.Zxyy.entity.LoveReport;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    public LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好，我是Zxyy宇";
        String answer = loveApp.doChat(message, chatId);
        //第二轮
        message = "我刚谈了一个女朋友 她叫散尽，我想送她100天礼物，该送什么好？";
        answer = loveApp.doChat(message, chatId);
        //第三轮
        message = "我和我的另一半叫什么？";
        answer = loveApp.doChat(message, chatId);
    }

    @Test
    void testWho() {
        loveApp.who();
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好，我是Zxyy宇 我想给我女朋友准备恋爱惊喜";
        LoveReport answer = loveApp.doChatWithReport(message, chatId);
    }



}