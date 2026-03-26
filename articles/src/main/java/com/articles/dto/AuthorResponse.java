package com.articles.dto;

import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String username,
        String name
) {
}
