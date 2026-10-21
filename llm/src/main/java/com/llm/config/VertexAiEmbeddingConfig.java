package com.llm.config;

import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines vertex ai embedding config.
 */
@Configuration
public class VertexAiEmbeddingConfig {

    @Value( "${spring.ai.vertex.ai.embedding.project-id}")
    private String projectId;

    @Value( "${spring.ai.vertex.ai.embedding.location}")
    private String location;

    @Value( "${spring.ai.vertex.ai.embedding.text.options.model}")
    private String model;

    @Bean
/**
 * Returns vertex ai text embedding options.
 * @return the result of the operation
 */
    public VertexAiTextEmbeddingOptions vertexAiTextEmbeddingOptions() {
        return VertexAiTextEmbeddingOptions.builder()
                .model(model)
                .build();
    }

    @Bean
/**
 * Returns vertex ai embedding connection details.
 * @return the result of the operation
 */
    public VertexAiEmbeddingConnectionDetails vertexAiEmbeddingConnectionDetails() {
        return VertexAiEmbeddingConnectionDetails.builder()
                .projectId(projectId)
                .location(location)
                .build();
    }

    @Bean
/**
 * Returns vertex ai text embedding model.
 * @param connectionDetails the connectionDetails
 * @param options the options
 * @return the result of the operation
 */
    public VertexAiTextEmbeddingModel vertexAiTextEmbeddingModel(VertexAiEmbeddingConnectionDetails connectionDetails, VertexAiTextEmbeddingOptions options) {
        return new VertexAiTextEmbeddingModel(connectionDetails, options);
    }
}
