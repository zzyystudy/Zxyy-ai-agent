package com.Zxyy.agent;

import cn.hutool.core.util.StrUtil;
import com.Zxyy.Memory.MyMemoryInMysql;
import com.Zxyy.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理 用于管理代理状态和执行流程
 * 步骤的定义
 * 循环的定义
 *
 * 子类必须实现 step()方法
 */

@Data
@Slf4j
public abstract class BaseAgent {

    //名称
    private String name;

    //提示词
    private String systemPrompt;
    //仿照 openminus 下一步提示词
    private String nextPrompt;

    //执行状态
    private AgentState state = AgentState.IDLE;

    //执行步数和最大指定步数
    private int currentStep = 0;
    private int maxSteps = 15;


    //LLM 大模型 (预留大模型 为了统一管理)

    private ChatClient chatClient;

    //Memory 存储 自主维护上下文
    List<Message> messageList = new ArrayList<>();
    private MyMemoryInMysql myMemoryInMysql;

    /**
     * 运行代理
     * @param userPrompt
     * @return
     */
    public String run(String userPrompt){

        if(this.state != AgentState.IDLE){
            throw new RuntimeException("Cannot run agent form state:" + this.state);
        }
        //如果是空不执行
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("Cannot run agent with empty userPrompt");
        }
        //执行
        this.state = AgentState.RUNNING;
        //记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        //保存结果列表
        List<String> results = new ArrayList<>();
        try {
            //执行循环
            for(int i = 0; i < maxSteps && this.state == AgentState.RUNNING; i++){
                int stepNum = i + 1;
                currentStep = stepNum;
                log.info("Running step {}/{}", stepNum,maxSteps);
                //单步执行得到结果
                String result = step();
                String stepResult = "Step " + stepNum + ": " + result;
                results.add(stepResult);
            }

            if(currentStep>=maxSteps){
                state = AgentState.FINISHED;
                results.add("Max steps reached. Stopping.");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error running agent: ", e);
            return "Error running agent: " + e.getMessage();
        } finally {
            //清理资源
            cleanup();
        }

    }


    /**
     * 定义每一步的方法
     * @return
     */
    public abstract String step();


    /**
     * 清理资源
     */
    protected void cleanup(){

    }



}
