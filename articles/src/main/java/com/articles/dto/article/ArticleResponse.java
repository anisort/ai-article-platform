package com.articles.dto.article;

import com.articles.entities.Article;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Carries article response.
 */
public record ArticleResponse(
        UUID id,
        String title,
        String imageUrl,
        String summary,
        String markdownContent,
        ZonedDateTime createdAt,
        boolean published,
        long likesCount,
        boolean likedByRequester
) implements Serializable {
/**
 * Returns from entity.
 * @param a the a
 * @param likesCount the likesCount
 * @param likedByRequester the likedByRequester
 * @return the result of the operation
 */
    public static ArticleResponse fromEntity(Article a, long likesCount, boolean likedByRequester) {
        return new ArticleResponse(
                a.getId(),
                a.getTitle(),
                a.getImageUrl(),
                a.getSummary(),
                a.getMarkdownContent(),
                a.getCreatedAt(),
                a.isPublished(),
                likesCount,
                likedByRequester
        );
    }
}
