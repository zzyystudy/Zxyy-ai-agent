package com.Zxyy.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
public class PGvectorConfig {

    @Autowired
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyKeyWordEnricher myKeyWordEnricher;

    @Bean
    public VectorStore PGVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        //这里是配置我们d
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1024)                    // Optional: defaults to model dimensions or 1536
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // 架构名称: defaults to "public"
                .vectorTableName("vector_store")     // 数据表的名称: defaults to "vector_store"
                                                        //一次处理文档的批次大小: defaults to 10 阿里云百炼平台的模型一次最多次传10条处理
                .maxDocumentBatchSize(10)                // 这个属性完全没有用 springai里面不会给你去做处理 当我们的documents太多的时候 只能自己分批处理
                .build();
        //这里我们可以加载一下我们的文档
        /*List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        List<Document> advancedDocuments = myKeyWordEnricher.enrichDocuments(documents);

        //这里必须进行分批次处理 否则会报错
        int batchSize = 10;
        for (int i = 0; i < advancedDocuments.size(); i += batchSize) {
            int end = Math.min(i + batchSize, advancedDocuments.size());
            List<Document> batch = advancedDocuments.subList(i, end);
            System.out.println("正在添加批次 " + (i/batchSize + 1) + "，大小: " + batch.size());
            vectorStore.add(batch);  // 这里还报错吗？
        }*/


        /*List<Document> documents = List.of(
                new Document("Zxyy是一个乐观开朗的人", Map.of("meta1", "meta1")),
                new Document("Zxyy是一名程序员", Map.of("meta2", "meta2")),
                new Document("Zxyy是一个帅哥", Map.of("meta3", "meta3"))
        );*/

        return vectorStore;
    }


}
