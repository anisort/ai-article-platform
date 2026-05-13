import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MarkdownModule } from 'ngx-markdown';

import { AuthService } from '../../../../core/services/auth.service';
import { ArticlesService } from '../../services/articles.service';
import { ArticleWithUserResponse } from '../../models/article-response.model';
import { MyArticlesService } from '../../../profile/services/my-articles.service';

@Component({
  selector: 'app-article-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink, MarkdownModule, DatePipe],
  templateUrl: './article-detail.page.html',
  styleUrl: './article-detail.page.scss',
})
export class ArticleDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly articlesService = inject(ArticlesService);
  private readonly authService = inject(AuthService);
  private readonly myArticlesService = inject(MyArticlesService);

  article = signal<ArticleWithUserResponse | null>(null);
  loading = signal(true);
  notFound = signal(false);
  likeLoading = signal(false);
  liked = signal(false);

  readonly articleData = computed(() => this.article()?.articleResponse ?? null);
  readonly authorData = computed(() => this.article()?.authorResponse ?? null);

  ngOnInit(): void {
    const articleId = this.route.snapshot.paramMap.get('id');

    if (!articleId) {
      this.loading.set(false);
      this.notFound.set(true);
      return;
    }

    this.articlesService.getById(articleId).subscribe({
      next: article => {
        this.article.set(article);
        this.loading.set(false);
        this.loadLikeState(article.articleResponse.id);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.notFound.set(error.status === 404);
      },
    });
  }

  authorName(): string {
    const author = this.authorData();
    return author?.name || author?.username || 'Unknown author';
  }

  canLike(): boolean {
    return this.authService.isAuthenticated();
  }

  toggleLike(): void {
    const articleId = this.articleData()?.id;

    if (!articleId || !this.canLike() || this.likeLoading()) {
      return;
    }

    this.likeLoading.set(true);
    this.myArticlesService.toggleLike(articleId).subscribe({
      next: response => {
        this.liked.set(response.liked);
        this.likeLoading.set(false);
      },
      error: () => {
        this.likeLoading.set(false);
      },
    });
  }

  contentMarkdown(): string {
    const article = this.articleData();
    if (!article) {
      return '';
    }
    return article.markdownContent ?? '';
  }

  private loadLikeState(articleId: string): void {
    if (!this.canLike()) {
      this.liked.set(false);
      return;
    }

    this.myArticlesService.listLikes().subscribe({
      next: response => {
        this.liked.set(response.articleIds.includes(articleId));
      },
      error: () => {
        this.liked.set(false);
      },
    });
  }
}
