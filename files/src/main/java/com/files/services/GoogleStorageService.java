package com.files.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * Defines google storage service.
 */
@Service
public class GoogleStorageService {

    @Value("${google.storage.bucket}")
    private String bucketName;

    private final Storage storage;

/**
 * Performs google storage service.
 * @param storage the storage
 */
    public GoogleStorageService(Storage storage) {
        this.storage = storage;
    }

/**
 * Returns upload file.
 * @param file the file
 * @return the result of the operation
 */
    public String uploadFile(MultipartFile file) throws IOException {
        String objectName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }

/**
 * Performs delete by url.
 * @param url the url
 */
    public void deleteByUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return;
        }
        URI uri = URI.create(url);
        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            return;
        }
        String normalized = path.startsWith("/") ? path.substring(1) : path;
        String prefix = bucketName + "/";
        if (!normalized.startsWith(prefix)) {
            return;
        }
        String objectName = normalized.substring(prefix.length());
        if (objectName.isBlank()) {
            return;
        }
        storage.delete(BlobId.of(bucketName, objectName));
    }
}
