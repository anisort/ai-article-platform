package com.articles.api;

import com.articles.dto.article.ArticleWithUserResponse;
import com.articles.dto.like.LikedArticlesResponse;
import com.articles.dto.like.ToggleArticleLikeResponse;
import com.articles.services.facade.MyArticleFacadeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/articles/me")
public class MyArticleApi {

    private final MyArticleFacadeService myArticleFacadeService;

    public MyArticleApi(MyArticleFacadeService myArticleFacadeService) {
        this.myArticleFacadeService = myArticleFacadeService;
    }

    @GetMapping
    public List<ArticleWithUserResponse> getMyArticles(@AuthenticationPrincipal Jwt jwt) {
        UUID authorId = UUID.fromString(jwt.getSubject());
        return myArticleFacadeService.getMyArticles(authorId);
    }

    @GetMapping("/likes")
    public LikedArticlesResponse getMyLikedArticles(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return myArticleFacadeService.getLikedArticles(userId);
    }

    @PostMapping("/{articleId}/like")
    public ToggleArticleLikeResponse toggleLike(
            @PathVariable UUID articleId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        return myArticleFacadeService.toggleLike(articleId, userId);
    }
}
