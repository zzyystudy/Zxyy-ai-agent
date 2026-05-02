package com.Zxyy.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PGvectorConfigTest {

    @Resource(name = "PGVectorStore")
    private VectorStore pgVectorStore;

    @Test
    void pgVectorStoreStore(){
        List<Document> documents = List.of(
                new Document("Zxyy是一个乐观开朗的人", Map.of("meta1", "meta1")),
                new Document("Zxyy是一名程序员", Map.of("meta2", "meta2")),
                new Document("Zxyy是一个帅哥", Map.of("meta3", "meta3"))
        );
        pgVectorStore.add(documents);
        List<Document> result = pgVectorStore.similaritySearch(SearchRequest.builder().query("Zxyy长得帅吗").topK(2).build());
        Assertions.assertNotNull(result);

    }




}