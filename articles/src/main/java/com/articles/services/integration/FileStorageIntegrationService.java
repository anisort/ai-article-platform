package com.articles.services.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Defines file storage integration service.
 */
@Service
public class FileStorageIntegrationService {

    private final RestClient filesRestClient;

/**
 * Performs file storage integration service.
 * @return the result of the operation
 */
    public FileStorageIntegrationService(@Qualifier("filesRestClient") RestClient filesRestClient) {
        this.filesRestClient = filesRestClient;
    }

/**
 * Performs delete by url.
 * @param imageUrl the imageUrl
 * @param bearerToken the bearerToken
 */
    public void deleteByUrl(String imageUrl, String bearerToken) {
        filesRestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/files").queryParam("url", imageUrl).build())
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .toBodilessEntity();
    }
}





