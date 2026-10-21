package com.files.api;

import com.files.dto.FileResponse;
import com.files.services.GoogleStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Defines google storage api.
 */
@RestController
@RequestMapping("/api/v1/files")
public class GoogleStorageApi {

    private final GoogleStorageService googleStorageService;

/**
 * Performs google storage api.
 * @param googleStorageService the googleStorageService
 */
    public GoogleStorageApi(GoogleStorageService googleStorageService) {
        this.googleStorageService = googleStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
/**
 * Returns upload file.
 * @return the result of the operation
 */
    public FileResponse uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return FileResponse.from(googleStorageService.uploadFile(file));
    }

    @DeleteMapping
/**
 * Performs delete file.
 */
    public void deleteFile(@RequestParam("url") String url) {
        googleStorageService.deleteByUrl(url);
    }
}
