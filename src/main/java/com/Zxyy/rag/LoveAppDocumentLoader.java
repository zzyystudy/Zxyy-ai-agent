package com.Zxyy.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 将文件转换为Document对象 我们的rag知识检索 检索的就是一个一个document所以需要将文件转换为Document对象
 * 先是要转化为document对象 再向量存储
 */
@Component
public class LoveAppDocumentLoader {

    private ResourcePatternResolver resourcePatternResolver;

    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        //这个是用来将我们的文件转换为对应的Resource对象的
        this.resourcePatternResolver = resourcePatternResolver;
    }

    List<Document> loadMarkdowns() {
        List<Document> documents = new ArrayList<>();
        Resource[] resources = null;
        try {
            //1.将文件作为资源对象加载到resources中
            resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for(Resource resource : resources){
                //2.将markdown文件转换为Document对象
                // 这里会将文件进行拆分为多个documents
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", resource.getFilename())
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                //3.将Document对象添加到documents中 这里的.get方法会返回一个list<Doucment>列表
                documents.addAll(reader.get());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       return documents;
    }
}
