package com.Zxyy.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.Zxyy.agent.model.AgentState;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础类
 * 这里我们要根据大模型思考的结果手动调用工具
 * 而不是依赖spring ai框架的调用
 */
@Slf4j
@Data
public class ToolCallAgent extends ReactAgent{

    //定义可用工具
    private final ToolCallback[] allTools;

    //保存要执行的工具临时列表
    //这个类里面会保存我们工具调用的列表 所以我们保存这个就可以了
    private ChatResponse toolCallResponse;

    //工具管理执行类 因为我们需要自己控制工具的调用
    private final ToolCallingManager toolCallingManager;

    //工具调用选项 关闭自主工具调用
    private final ToolCallingChatOptions chatOptions;

    //提示词copy
    //这里是获取Meaasge信息
     //instructions = chatClientRequest.prompt().getInstructions();

    public ToolCallAgent(ToolCallback[] allTools, ToolCallingManager toolCallingManager){
        super();
        this.allTools = allTools;
        this.toolCallingManager = toolCallingManager;
        this.chatOptions = DashScopeChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
    }


    @Override
    public boolean think() {
        //校验 提示词 拼接提示词
        if(StrUtil.isBlank(getNextPrompt())){
            UserMessage userMessage = new UserMessage(getSystemPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messages = getMessageList();
        Prompt prompt = new Prompt(messages,chatOptions);
        try {
            //调用AI大模型 获取提示词结果工具调用的结果
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(allTools)
                    .call()
                    .chatResponse();
            //记录回答
            this.toolCallResponse = chatResponse;
            //解析工具调用结果 获取要调用的工具
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //获取要调用的工具列表
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            //输出提示信息
            String text = assistantMessage.getText();

            log.info("AI思考结果为:{}",text);
            log.info("AI选择的工具数量为:{}",toolCalls.size());
            String collect = toolCalls.stream()
                    .map(toolCall -> String.format("工具名称为:%s,参数为:%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info("AI选择的工具为:{}",collect);
            //如果不需要调用工具返回false
            if(toolCalls.isEmpty()){
                //添加助手信息
                getMessageList().add(assistantMessage);
                return false;
            }else {
                //这里不用添加信息 工具调用会自动拼接信息
                return true;
                //return 后续时act调用
            }
        } catch (Exception e) {
            //打印日志
            log.error("{}大模型调用出现了问题:{}",getName(),e.getMessage());
            getMessageList().add(new AssistantMessage("处理时出现了问题"+e.getMessage()));
            return false;
        }

    }

    @Override
    public String act() {
        if(!toolCallResponse.hasToolCalls()){
            return "没有工具调用";
        }
        Prompt prompt = new Prompt(getMessageList(),chatOptions);

        //调用工具
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        //记录消息上下文 工具调用内部会自动记录工具调用的消息 这里我们直接获取并取代
        setMessageList(toolExecutionResult.conversationHistory());
        //将调用结果返回给外层
        //获取最后一条消息
        ToolResponseMessage toolResponseMessage =(ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String result = toolResponseMessage.getResponses().stream()
                .map(toolResponse -> StrUtil.format("工具{}调用结果为:{}", toolResponse.name(), toolResponse.responseData()))
                .collect(Collectors.joining("\n"));
        log.info("工具调用结果为:{}",result);
        //判断工具调用结果是否包含了结束工具 包含就将大模型结果改为finished
        if(toolResponseMessage.getResponses().stream().anyMatch(toolResponse -> toolResponse.name().equals("doTerminate"))){
            setState(AgentState.FINISHED);
        }
        return result;
    }
}
