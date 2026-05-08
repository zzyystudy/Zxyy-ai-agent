package com.Zxyy.tools;

import org.springframework.ai.tool.annotation.Tool;

/**
 * 让自主规划的智能体能够停止
 }
 */
public class TerminateTool {
    @Tool(description = """
            Terminate the interaction when the request is met oR if the assistant cannot proceed further with the task.
            When you have finished all the tasks, call this tool to end the work.
            When you don't have to use tools to finish the task, please call this call to end the work or you will lose in a loop
            """)
    public String doTerminate() {
        return "任务结束";
    }
}
