package com.articles.services.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FileStorageService {

    private final RestClient filesRestClient;

    public FileStorageService(@Qualifier("filesRestClient") RestClient filesRestClient) {
        this.filesRestClient = filesRestClient;
    }

    public void deleteByUrl(String imageUrl, String bearerToken) {
        filesRestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/files").queryParam("url", imageUrl).build())
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .toBodilessEntity();
    }
}





