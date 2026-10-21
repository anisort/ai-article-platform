package com.files.dto;

/**
 * Carries file response.
 */
public record FileResponse(
        String url
) {
/**
 * Returns from.
 * @param url the url
 * @return the result of the operation
 */
    public static FileResponse from(String url) {
        if (url == null) return null;
        return new FileResponse(url);
    }
}
