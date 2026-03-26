package com.files.dto;

public record FileResponse(
        String url
) {
    public static FileResponse from(String url) {
        if (url == null) return null;
        return new FileResponse(url);
    }
}
