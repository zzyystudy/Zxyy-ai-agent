package com.Zxyy.controller;

import com.Zxyy.agent.ZxyyAgent;
import com.Zxyy.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    private LoveApp loveApp;

    //这里不能自动注入 因为每一次访问这个就扣都要重新创建一个 ZxyyAgent对象
    //我们如果注入 这个对象 就只有一个实例 是不对的
    /*@Autowired
    private ZxyyAgent zxyyAgent;
*/

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ToolCallingManager toolCallingManager;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String chat(String message,String chatId){
        return loveApp.doChat(message,chatId);
    }

    @GetMapping(value = "/love_app/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatSse(String message, String chatId){
        return loveApp.doChatWithStream(message,chatId);
    }

    @GetMapping("/Manus/chat")
    public SseEmitter ManusChat(String message){
        ZxyyAgent zxyyAgent = new ZxyyAgent(allTools, toolCallingManager, dashscopeChatModel);
        return zxyyAgent.runSse(message);
    }




}
