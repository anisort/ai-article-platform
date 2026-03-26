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

    @Transactional
    public Article create(CreateArticleRequest request, UUID authorId) {
        Article article = new Article();
        article.setAuthorId(authorId);
        article.setTitle(request.title());
        article.setImageUrl(request.imageUrl());
        article.setPublished(false);
        article.setSummary(null);

        request.blocks().stream()
                .sorted(Comparator.comparingInt(CreateArticleBlockRequest::position))
                .forEach(block -> article.getBlocks().add(toEntity(article, block)));

        Article saved = articleRepository.save(article);

        // return extracted text and create embedding
        extractText(saved);

        return saved;
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
