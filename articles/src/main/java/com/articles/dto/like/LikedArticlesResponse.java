package com.articles.dto.like;

import java.util.List;
import java.util.UUID;

public record LikedArticlesResponse(List<UUID> articleIds) {
}
