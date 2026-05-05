package com.Zxyy.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.Zxyy.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class ResourceDownloadTool {
    @Tool(description = "Download a resource from a given URL")
    String downloadResource(@ToolParam(description = "URL of the resource to download") String url,
                            @ToolParam(description = "Name of the file to save the resource as") String filename) {
        String filePath = FileConstant.FILE_PATH + File.separator +"download";
        String fileName = filePath + File.separator + filename;
        try {
            HttpUtil.downloadFile(url, new File(fileName));
            return "Resource downloaded successfully to " + fileName;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return "Error downloading resource: " + e.getMessage();
        }
    }
}
