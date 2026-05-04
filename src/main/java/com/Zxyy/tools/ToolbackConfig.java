package com.Zxyy.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * 工具配置类
 */
@Configuration
public class ToolbackConfig {

    @Bean
    public ToolCallback[] allTools(){
        DateTimeTool dateTimeTool = new DateTimeTool();
        FileReadAndWriteTool fileReadAndWriteTool = new FileReadAndWriteTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        return ToolCallbacks.from(
                dateTimeTool,
                fileReadAndWriteTool,
                resourceDownloadTool);

    }

}
