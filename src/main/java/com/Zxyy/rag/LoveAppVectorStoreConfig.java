package com.Zxyy.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoveAppVectorStoreConfig {

    private final LoveAppDocumentLoader loveAppDocumentLoader;
    LoveAppVectorStoreConfig(LoveAppDocumentLoader loveAppDocumentLoader) {
        this.loveAppDocumentLoader = loveAppDocumentLoader;
    }

    //@Bean
    public VectorStore loveAppVectorStore(EmbeddingModel embeddingModel) {
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}
