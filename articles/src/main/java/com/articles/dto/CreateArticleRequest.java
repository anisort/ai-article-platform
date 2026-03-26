package com.articles.dto;

import java.util.List;
import java.util.UUID;

public record CreateArticleRequest(
        String title,
        String imageUrl,
        boolean published,
        List<CreateArticleBlockRequest> blocks
) {}

