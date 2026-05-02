package com.Zxyy.advisor;

import com.Zxyy.constant.BannedWordConstant;
import com.Zxyy.exception.BannedWordException;
import com.Zxyy.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

/**
 * 敏感词检测拦截器
 */
@Slf4j
public class CheckAdvisor implements CallAdvisor, StreamAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        try {
            check(chatClientRequest);
        }catch (BaseException e){
            throw new BannedWordException("包含敏感词汇，请修改后重试");
        }


        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);


        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
                                                 StreamAdvisorChain streamAdvisorChain) {

        //流式响应的异常处理
        try {
            check(chatClientRequest);
        }catch (BaseException e) {
            return Flux.error(e);
        }

        //这里是流式调用
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);

        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, new Consumer<ChatClientResponse>() {
            @Override
            public void accept(ChatClientResponse chatClientResponse) {

            }
        });
    }


    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public String toString() {
        return MyLoggerAdvisor.class.getSimpleName();
    }

    /**
     * 检查是否包含敏感词
     * @param chatClientRequest
     * @throws BaseException
     */
    private void check(ChatClientRequest chatClientRequest) throws BaseException {
        List<Message> messages = chatClientRequest.prompt().getInstructions();

        for (Message message : messages) {
            if (message instanceof UserMessage) {
                String content = message.getText();

                for (String sensitiveWord : BannedWordConstant.BANNED_WORDS) {
                    if (content.contains(sensitiveWord)) {
                        //log.warn("检测到敏感词: {}, 用户输入: {}", sensitiveWord, content);
                        throw new BannedWordException("包含敏感词汇，请修改后重试");
                    }
                }
            }
        }
    }
}
