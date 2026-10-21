package com.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Defines cors config.
 */
@Configuration
public class CorsConfig {
    @Value("${cors.frontend-url-host}")
    private String frontendUrlHost;

    @Bean
/**
 * Returns cors configurer.
 * @return the result of the operation
 */
    public WebMvcConfigurer corsConfigurer() {
/**
 * Returns web mvc configurer.
 * @return the result of the operation
 */
        return new WebMvcConfigurer() {
            @Override
/**
 * Performs add cors mappings.
 */
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontendUrlHost)
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
