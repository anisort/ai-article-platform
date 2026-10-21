package com.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Defines rest client config.
 */
@Configuration
public class RestClientConfig {

    @Bean
/**
 * Returns rest client builder.
 * @return the result of the operation
 */
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
