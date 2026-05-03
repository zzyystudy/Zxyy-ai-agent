package com.Zxyy.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetrievalAugmentationAdvisorFactury {

    public static RetrievalAugmentationAdvisor create(VectorStore vectorStore) {

        //{"title": "怎样在社交场合主动结识心仪异性？", "category": "header_4", "filename": "恋爱常见问题和回答 - 单身篇.md", "excerpt_keywords": "社交技巧, 主动沟通, 倾听反馈, 行业交流, 交友课程"}
        //恋爱常见问题和回答 - 单身篇.md
        /*DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.73)
                .topK(5)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("category", "header_4")
                        .build())
                .build();
        List<Document> documents = retriever.retrieve(new Query("我单身怎么办？"));*/
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .filterExpression(new FilterExpressionBuilder()
                                //.eq("category", "header_3")                   //这里是匹配标签为 category=header_3 的文档
                                .eq("filename", "恋爱常见问题和回答 - 恋爱篇.md")
                                .build())
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();
        return (RetrievalAugmentationAdvisor) retrievalAugmentationAdvisor;
    }
}
