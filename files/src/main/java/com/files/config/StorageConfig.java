package com.files.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines storage config.
 */
@Configuration
public class StorageConfig {

    @Bean
/**
 * Returns storage.
 * @return the result of the operation
 */
    public Storage storage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}
