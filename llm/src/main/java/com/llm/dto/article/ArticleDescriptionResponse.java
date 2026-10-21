package com.llm.dto.article;

/**
 * Carries article description response.
 */
/**
 * Performs article description response.
 * @param description the description
 * @return the result of the operation
 */
public record ArticleDescriptionResponse(String description) {
/**
 * Returns of.
 * @param description the description
 * @return the result of the operation
 */
    public static ArticleDescriptionResponse of(String description) {
        return new ArticleDescriptionResponse(description);
    }
}
