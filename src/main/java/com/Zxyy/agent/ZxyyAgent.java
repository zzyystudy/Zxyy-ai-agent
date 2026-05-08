package com.Zxyy.agent;

import com.Zxyy.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class ZxyyAgent extends ToolCallAgent{

    public ZxyyAgent(ToolCallback[] allTools, ToolCallingManager toolCallingManager, ChatModel dashscopeChatModel) {
        super(allTools, toolCallingManager);
        this.setName("ZxyyAgent");
        String SYSTEM_PROMPT = """
                You are ZxyyAgent, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;

        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT ="""
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the 'terminate' tool/function call.
                """;

        this.setNextPrompt(NEXT_STEP_PROMPT);
        //初始化ai客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .build();

        this.setChatClient(chatClient);

    }
}
