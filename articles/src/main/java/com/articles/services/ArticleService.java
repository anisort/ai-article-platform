package com.articles.services;

import com.articles.dto.CreateArticleBlockRequest;
import com.articles.dto.CreateArticleRequest;
import com.articles.entities.Article;
import com.articles.entities.ArticleBlock;
import com.articles.repositories.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    public List<Article> getPublishedArticles() {
        return articleRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<Article> getArticlesByAuthor(UUID authorId) {
        return articleRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    public List<Article> getPublishedArticlesByAuthor(UUID authorId) {
        return articleRepository.findByAuthorIdAndPublishedTrueOrderByCreatedAtDesc(authorId);
    }

    public Optional<Article> getPublishedArticle(UUID articleId) {
        return articleRepository.findByIdAndPublishedTrue(articleId);
    }

    public Optional<Article> getById(UUID articleId) {
        return articleRepository.findById(articleId);
    }

    @Transactional
    public Article create(CreateArticleRequest request, UUID authorId) {
        Article article = new Article();
        article.setAuthorId(authorId);
        applyRequest(article, request);

        Article saved = articleRepository.save(article);

        // return extracted text and create embedding
        extractText(saved);

        return saved;
    }

    @Transactional
    public Article update(Article article, CreateArticleRequest request) {
        applyRequest(article, request);

        Article saved = articleRepository.save(article);
        extractText(saved);

        return saved;
    }

    private void applyRequest(Article article, CreateArticleRequest request) {
        article.setTitle(request.title());
        article.setImageUrl(request.imageUrl());
        article.setPublished(request.published());
        article.setSummary(null);
        article.getBlocks().clear();

        request.blocks().stream()
                .sorted(Comparator.comparingInt(CreateArticleBlockRequest::position))
                .forEach(block -> article.getBlocks().add(toEntity(article, block)));
    }

    private ArticleBlock toEntity(Article article, CreateArticleBlockRequest b) {
        ArticleBlock block = new ArticleBlock();
        block.setArticle(article);
        block.setPosition(b.position());
        block.setType(b.type());

        block.setContent(b.content());
        block.setCodeLanguage(b.codeLanguage());
        block.setCode(b.code());
        block.setImageUrl(b.imageUrl());
        block.setImageCaption(b.imageCaption());

        return block;
    }

    public static String extractText(Article article) {
        return "text";
    }

}
