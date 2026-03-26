package com.files.api;

import com.files.dto.FileResponse;
import com.files.services.GoogleStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class GoogleStorageApi {

    private final GoogleStorageService googleStorageService;

    public GoogleStorageApi(GoogleStorageService googleStorageService) {
        this.googleStorageService = googleStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileResponse uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return FileResponse.from(googleStorageService.uploadFile(file));
    }
}
