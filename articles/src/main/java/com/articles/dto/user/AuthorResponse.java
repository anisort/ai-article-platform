package com.articles.dto.user;

import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String username,
        String name
) {
}
