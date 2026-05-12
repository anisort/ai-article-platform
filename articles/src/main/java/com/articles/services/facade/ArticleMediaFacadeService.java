package com.articles.services.facade;

import com.articles.dto.image.ArticleImageResponse;
import com.articles.entities.ArticleImage;
import com.articles.services.core.ArticleImageService;
import com.articles.services.core.ArticleService;
import com.articles.services.integration.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ArticleMediaFacadeService {

    private final ArticleService articleService;
    private final ArticleImageService articleImageService;
    private final FileStorageService fileStorageService;

    public ArticleMediaFacadeService(
            ArticleService articleService,
            ArticleImageService articleImageService,
            FileStorageService fileStorageService
    ) {
        this.articleService = articleService;
        this.articleImageService = articleImageService;
        this.fileStorageService = fileStorageService;
    }

    public List<ArticleImageResponse> listArticleImages(UUID articleId, UUID requesterId) {
        articleService.requireOwned(articleId, requesterId);
        return articleImageService.listByArticleId(articleId).stream().map(ArticleImageResponse::fromEntity).toList();
    }

    public ArticleImageResponse addArticleImage(UUID articleId, UUID requesterId, String imageUrl) {
        articleService.requireOwned(articleId, requesterId);
        if (!StringUtils.hasText(imageUrl)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imageUrl is required");
        }
        ArticleImage image = articleImageService.add(articleId, imageUrl);
        return ArticleImageResponse.fromEntity(image);
    }

    public void deleteArticleImage(UUID articleId, UUID imageId, UUID requesterId, String bearerToken) {
        articleService.requireOwned(articleId, requesterId);
        ArticleImage image = articleImageService.findByIdAndArticleId(imageId, articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        fileStorageService.deleteByUrl(image.getImageUrl(), bearerToken);
        articleImageService.delete(image);
    }
}





