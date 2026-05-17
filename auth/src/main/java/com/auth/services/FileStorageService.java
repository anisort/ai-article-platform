package com.auth.services;

import com.auth.dto.FileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class FileStorageService {

    private final RestClient restClient;

    public FileStorageService(RestClient.Builder restClientBuilder,
                              @Value("${files.api-url}") String filesApiUrl) {
        this.restClient = restClientBuilder.baseUrl(filesApiUrl).build();
    }

    public String upload(MultipartFile file) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            FileResponse response = restClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(FileResponse.class);

            if (response == null || !StringUtils.hasText(response.url())) {
                throw new ResponseStatusException(BAD_GATEWAY, "File service returned empty url");
            }

            return response.url();
        } catch (RuntimeException ex) {
            if (ex instanceof ResponseStatusException) {
                throw ex;
            }
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to upload avatar", ex);
        }
    }
}
