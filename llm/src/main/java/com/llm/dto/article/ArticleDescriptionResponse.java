package com.llm.dto.article;

public record ArticleDescriptionResponse(String description) {
    public static ArticleDescriptionResponse of(String description) {
        return new ArticleDescriptionResponse(description);
    }
}
