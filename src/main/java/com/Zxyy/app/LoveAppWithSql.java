package com.Zxyy.app;

import com.Zxyy.Memory.MyMemoryInMysql;
import com.Zxyy.advisor.CheckAdvisor;
import com.Zxyy.advisor.MyLoggerAdvisor;
import com.Zxyy.entity.LoveReport;
import com.Zxyy.rag.QueryRewriter;
import com.Zxyy.rag.RetrievalAugmentationAdvisorFactury;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class LoveAppWithSql {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是资深专业恋爱情感大师，精通两性心理、聊天话术、暧昧升温、相处边界、矛盾化解、挽回关系、择偶判断、情商沟通。";



    /**
     * 构造函数
     * @param dashscopeChatModel
     */
    public LoveAppWithSql(ChatModel dashscopeChatModel,MyMemoryInMysql myMemoryInMysql) {
        //基于内存的对话记忆
        //1.这里创建了一个chatMemory对象
        //ChatMemory chatMemory = myMemoryInMysql;
        //这里是基于内存的对话记忆功能
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();


        //这里创建一个 spring Ai提供好的advisor对象 我们要学会查看文档
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        //这里我们构建一个chatClient对象  这里我们使用了上面的大模型  这里spring会将这个，模型注入进来
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(messageChatMemoryAdvisor, //记忆化存储advisor 这个是mysql实现的
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
        //log.info("User:{}",message);
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
        //log.info("AI:{}",content);
        return content;
    }


    // RAG 知识问答

    //private VectorStore loveAppVectorStore;
    //text-embedding-v3 这是里面调用的embedding模型

    @Resource
    private VectorStore PGVectorStore;

    @Resource
    private QueryRewriter queryRewriter;


    /**
     * 带rag的问答
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message,String chatId){
        String rewritemessage = queryRewriter.rewrite(message);
        ChatResponse response = chatClient
                .prompt()
                //.advisors(QuestionAnswerAdvisor.builder(PGVectorStore).build())
                .advisors(RetrievalAugmentationAdvisorFactury.create(PGVectorStore))
                .user(rewritemessage)
                .call()
                .chatResponse();
        return response.getResults().get(0).getOutput().getText();
    }



}
