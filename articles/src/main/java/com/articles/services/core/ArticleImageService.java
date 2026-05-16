package com.articles.services.core;

import com.articles.entities.ArticleImage;
import com.articles.repositories.ArticleImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleImageService {

    private final ArticleImageRepository articleImageRepository;

    public ArticleImageService(ArticleImageRepository articleImageRepository) {
        this.articleImageRepository = articleImageRepository;
    }

    public List<ArticleImage> listByArticleId(UUID articleId) {
        return articleImageRepository.findByArticleIdOrderByIdDesc(articleId);
    }

    public ArticleImage add(UUID articleId, String imageUrl) {
        ArticleImage image = new ArticleImage();
        image.setArticleId(articleId);
        image.setImageUrl(imageUrl);
        return articleImageRepository.save(image);
    }

    public Optional<ArticleImage> findByIdAndArticleId(UUID imageId, UUID articleId) {
        return articleImageRepository.findByIdAndArticleId(imageId, articleId);
    }

    public void delete(ArticleImage image) {
        articleImageRepository.delete(image);
    }

    @Transactional
    public void deleteByArticleId(UUID articleId) {
        articleImageRepository.deleteByArticleId(articleId);
    }
}
