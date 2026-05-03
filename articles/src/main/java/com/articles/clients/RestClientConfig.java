package com.articles.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private final String userServiceUrl;
    private final String llmServiceUrl;

    public RestClientConfig(
            @Value("${services.user.url}") String userServiceUrl,
            @Value("${services.llm.url}") String llmServiceUrl
    ) {
        this.userServiceUrl = userServiceUrl;
        this.llmServiceUrl = llmServiceUrl;
    }

    @Bean("userRestClient")
    public RestClient userRestClient() {
        return RestClient.create(userServiceUrl);
    }

    @Bean("llmRestClient")
    public RestClient llmRestClient() {
        return RestClient.create(llmServiceUrl);
    }
}
