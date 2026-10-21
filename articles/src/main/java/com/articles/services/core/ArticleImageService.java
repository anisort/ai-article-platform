package com.articles.services.core;

import com.articles.entities.ArticleImage;
import com.articles.repositories.ArticleImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines article image service.
 */
@Service
public class ArticleImageService {

    private final ArticleImageRepository articleImageRepository;

/**
 * Performs article image service.
 * @param articleImageRepository the articleImageRepository
 */
    public ArticleImageService(ArticleImageRepository articleImageRepository) {
        this.articleImageRepository = articleImageRepository;
    }

/**
 * Returns list by article id.
 * @param articleId the articleId
 * @return the result of the operation
 */
    public List<ArticleImage> listByArticleId(UUID articleId) {
        return articleImageRepository.findByArticleIdOrderByIdDesc(articleId);
    }

/**
 * Returns add.
 * @param articleId the articleId
 * @param imageUrl the imageUrl
 * @return the result of the operation
 */
    public ArticleImage add(UUID articleId, String imageUrl) {
        ArticleImage image = new ArticleImage();
        image.setArticleId(articleId);
        image.setImageUrl(imageUrl);
        return articleImageRepository.save(image);
    }

/**
 * Returns find by id and article id.
 * @param imageId the imageId
 * @param articleId the articleId
 * @return the result of the operation
 */
    public Optional<ArticleImage> findByIdAndArticleId(UUID imageId, UUID articleId) {
        return articleImageRepository.findByIdAndArticleId(imageId, articleId);
    }

/**
 * Performs delete.
 * @param image the image
 */
    public void delete(ArticleImage image) {
        articleImageRepository.delete(image);
    }

    @Transactional
/**
 * Performs delete by article id.
 * @param articleId the articleId
 */
    public void deleteByArticleId(UUID articleId) {
        articleImageRepository.deleteByArticleId(articleId);
    }
}
