package com.articles.dto.user;

import java.io.Serializable;
import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String username,
        String name,
        String avatar
) implements Serializable {
}
