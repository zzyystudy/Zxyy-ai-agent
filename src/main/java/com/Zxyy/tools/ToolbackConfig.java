package com.Zxyy.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 工具配置类
 */
@Configuration
public class ToolbackConfig {

    @Resource
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    @Bean
    public ToolCallback[] allTools(){
        // 创建本地工具实例
        DateTimeTool dateTimeTool = new DateTimeTool();
        FileReadAndWriteTool fileReadAndWriteTool = new FileReadAndWriteTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();

        // 获取本地工具的ToolCallback数组
        ToolCallback[] localTools = ToolCallbacks.from(
                dateTimeTool,
                fileReadAndWriteTool,
                resourceDownloadTool);

        // 获取MCP工具
        ToolCallback[] mcpTools = toolCallbackProvider.getToolCallbacks();

        // 合并本地工具和MCP工具
        List<ToolCallback> allToolsList = new ArrayList<>();
        for (ToolCallback localTool : localTools) {
            allToolsList.add(localTool);
        }
        for (ToolCallback mcpTool : mcpTools) {
            allToolsList.add(mcpTool);
        }

        return allToolsList.toArray(new ToolCallback[0]);
    }

}
