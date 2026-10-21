package com.articles.services.facade;

import com.articles.dto.image.ArticleImageResponse;
import com.articles.entities.ArticleImage;
import com.articles.services.core.ArticleImageService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.FileStorageIntegrationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Defines article media facade service.
 */
@Service
public class ArticleMediaFacadeService {

    private final ArticleService articleService;
    private final ArticleImageService articleImageService;
    private final FileStorageIntegrationService fileStorageIntegrationService;

    public ArticleMediaFacadeService(
            ArticleService articleService,
            ArticleImageService articleImageService,
            FileStorageIntegrationService fileStorageIntegrationService
    ) {
        this.articleService = articleService;
        this.articleImageService = articleImageService;
        this.fileStorageIntegrationService = fileStorageIntegrationService;
    }

/**
 * Returns add article image.
 * @param articleId the articleId
 * @param requesterId the requesterId
 * @param imageUrl the imageUrl
 * @return the result of the operation
 */
    public ArticleImageResponse addArticleImage(UUID articleId, UUID requesterId, String imageUrl) {
        articleService.requireOwned(articleId, requesterId);
        if (!StringUtils.hasText(imageUrl)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imageUrl is required");
        }
        ArticleImage image = articleImageService.add(articleId, imageUrl);
        return ArticleImageResponse.fromEntity(image);
    }

/**
 * Performs delete article image.
 * @param articleId the articleId
 * @param imageId the imageId
 * @param requesterId the requesterId
 * @param bearerToken the bearerToken
 */
    public void deleteArticleImage(UUID articleId, UUID imageId, UUID requesterId, String bearerToken) {
        articleService.requireOwned(articleId, requesterId);
        ArticleImage image = articleImageService.findByIdAndArticleId(imageId, articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        fileStorageIntegrationService.deleteByUrl(image.getImageUrl(), bearerToken);
        articleImageService.delete(image);
    }
}





