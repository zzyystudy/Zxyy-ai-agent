package com.Zxyy.app;

import com.Zxyy.advisor.CheckAdvisor;
import com.Zxyy.advisor.MyLoggerAdvisor;
import com.Zxyy.entity.LoveReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@Slf4j
public class LoveApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是资深专业恋爱情感大师，精通两性心理、聊天话术、暧昧升温、相处边界、矛盾化解、挽回关系、择偶判断、情商沟通。";

    /**
     * 构造函数
     * @param dashscopeChatModel
     */
    public LoveApp(ChatModel dashscopeChatModel) {
        //基于内存的对话记忆
        //1.这里创建了一个chatMemory对象
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        //这里创建一个 spring Ai提供好的advisor对象 我们要学会查看文档
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
        //这里我们构建一个chatClient对象  这里我们使用了上面的大模型  这里spring会将这个，模型注入进来
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(messageChatMemoryAdvisor,//这里一定要将我们的顾问放进去
                        new MyLoggerAdvisor(),
                        new CheckAdvisor())  //自定义日志拦截器 slf4j info级别
                .build();
    }

    /**
     * AI 基础对话 实现多轮记忆
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message,String chatId){
        log.info("User:{}",message);
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(new Consumer<ChatClient.AdvisorSpec>() {
                    @Override
                    public void accept(ChatClient.AdvisorSpec advisorSpec) {
                        advisorSpec.param("chat_memory_conversation_id", chatId);
                    }
                })
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("AI:{}",content);
        return content;
    }

    public String who(){
        String text = chatClient.prompt()
                .user("你是谁？属于哪家公司")
                .call()
                .chatResponse().getResult().getOutput().getText();
        log.info("AI:{}",text);
        return text;
    }

    public LoveReport doChatWithReport(String message,String chatId){
        log.info("User:{}",message);
        LoveReport loveReport = chatClient.prompt()
                .user(message)
                .advisors(new Consumer<ChatClient.AdvisorSpec>() {
                    @Override
                    public void accept(ChatClient.AdvisorSpec advisorSpec) {
                        advisorSpec.param("chat_memory_conversation_id", chatId);
                    }
                })
                .call()
                .entity(LoveReport.class);

        String content = loveReport.toString();
        log.info("AI:{}",content);
        return loveReport;
    }
}
