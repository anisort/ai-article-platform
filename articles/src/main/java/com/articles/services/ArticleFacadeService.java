package com.articles.services;

import com.articles.dto.ArticleWithUserResponse;
import com.articles.dto.AuthorResponse;
import com.articles.dto.CreateArticleRequest;
import com.articles.entities.Article;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ArticleFacadeService {

    private final ArticleService articleService;
    private final UserService userService;

    public ArticleFacadeService(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    public List<ArticleWithUserResponse> getArticles() {
        List<Article> articles = articleService.getArticles();
        Map<UUID, AuthorResponse> authors = userService.getAuthors(articles);
        return articles.stream()
                .map(article -> ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId())))
                .toList();
    }

    public ArticleWithUserResponse create(CreateArticleRequest request, UUID authorId) {
        Article article = articleService.create(request, authorId);
        Map<UUID, AuthorResponse> authors = userService.getAuthors(List.of(article));
        return ArticleWithUserResponse.mapToResponse(article, authors.get(article.getAuthorId()));
    }
}
