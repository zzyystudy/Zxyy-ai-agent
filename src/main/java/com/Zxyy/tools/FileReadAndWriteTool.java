package com.Zxyy.tools;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.Zxyy.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileReadAndWriteTool {

    @Tool(description = "Read a file and return its contents")
    String readFile(@ToolParam(description = "Name of a file to read") String filename){
        String filePath = FileConstant.FILE_PATH + File.separator + filename;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (IORuntimeException e) {
            //throw new RuntimeException(e);
            return "Error read file: " + e.getMessage();
        }
    }

    @Tool(description = "Write a file")
    String writeFile(@ToolParam(description = "Name of a file to write") String filename,
                     @ToolParam(description = "Content to write") String content){
        String filePath = FileConstant.FILE_PATH + File.separator + filename;
        try {
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully";
        } catch (IORuntimeException e) {
            //throw new RuntimeException(e);
            return "Error writing file: " + e.getMessage();
        }
    }

}
