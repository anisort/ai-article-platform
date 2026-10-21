import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../../core/services/auth.service';
import { ArticlesService } from '../../services/articles.service';
import { ArticleWithUserResponse } from '../../models/article-response.model';
import { ProfileService } from '../../../profile/services/profile.service';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import { ProfileSummaryCardComponent } from '../../../profile/components/profile-summary-card/profile-summary-card.component';
import { UserProfile } from '../../../profile/models/user-profile.model';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { MarkdownRenderService } from '../../../../shared/services/markdown-render.service';

@Component({
  selector: 'app-article-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, PageIntroComponent, ProfileSummaryCardComponent, LoadingStateComponent],
  templateUrl: './article-detail.page.html',
  styleUrl: './article-detail.page.scss',
})
/**
 * Defines the article detail page class.
 */
export class ArticleDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly articlesService = inject(ArticlesService);
  private readonly authService = inject(AuthService);
  private readonly profileService = inject(ProfileService);
  private readonly markdownRenderService = inject(MarkdownRenderService);

  article = signal<ArticleWithUserResponse | null>(null);
  loading = signal(true);
  likeLoading = signal(false);
  deleteLoading = signal(false);
  liked = signal(false);
  likesCount = signal(0);

  readonly likesCountLabel = computed(() => this.formatLikesCount(this.likesCount()));

  readonly articleData = computed(() => this.article()?.articleResponse ?? null);
  readonly authorData = computed(() => this.article()?.authorResponse ?? null);
  readonly isAuthor = computed(() => {
    const currentUserId = this.authService.userId;
    const authorId = this.authorData()?.id;
    return !!currentUserId && !!authorId && currentUserId === authorId;
  });

  readonly authorProfile = computed<UserProfile>(() => {
    const author = this.authorData();
    return {
      id: author?.id ?? '',
      username: author?.username ?? '',
      name: author?.name ?? '',
      email: null,
      avatar: author?.avatar ?? null,
      ownProfile: false,
      following: false,
      followers: [],
      followingUsers: [],
    };
  });

/**
 * Performs the ng on init operation.
 */
  ngOnInit(): void {
    const articleId = this.route.snapshot.paramMap.get('id');

    if (!articleId) {
      this.router.navigate(['/not-found']);
      return;
    }

    this.articlesService.getById(articleId).subscribe({
      next: article => {
        this.article.set(article);
        this.liked.set(article.articleResponse.likedByRequester);
        this.likesCount.set(article.articleResponse.likesCount ?? 0);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        if (error.status === 404) {
          this.router.navigate(['/not-found']);
        }
      },
    });
  }

/**
 * Performs the can like operation.
 * @returns the operation result
 */
  canLike(): boolean {
    return this.authService.isAuthenticated();
  }

/**
 * Performs the toggle like operation.
 */
  toggleLike(): void {
    const articleId = this.articleData()?.id;

    if (!articleId || !this.canLike() || this.likeLoading()) {
      return;
    }

    this.likeLoading.set(true);
    this.profileService.toggleArticleLike(articleId).subscribe({
      next: response => {
        this.liked.set(response.liked);
        this.likesCount.set(response.likesCount);
        this.likeLoading.set(false);
      },
      error: () => {
        this.likeLoading.set(false);
      },
    });
  }

/**
 * Performs the delete article operation.
 */
  deleteArticle(): void {
    const articleId = this.articleData()?.id;
    if (!articleId || !this.isAuthor() || this.deleteLoading()) {
      return;
    }

    const shouldDelete = window.confirm('Delete this article? This action cannot be undone.');
    if (!shouldDelete) {
      return;
    }

    this.deleteLoading.set(true);
    this.articlesService.delete(articleId).subscribe({
      next: () => {
        this.deleteLoading.set(false);
        this.router.navigate(['/articles']);
      },
      error: () => {
        this.deleteLoading.set(false);
      },
    });
  }

/**
 * Performs the content markdown operation.
 * @returns the operation result
 */
  contentMarkdown(): string {
    const article = this.articleData();
    if (!article) {
      return '';
    }
    return this.markdownRenderService.render(article.markdownContent ?? '');
  }

/**
 * Performs the on article content click operation.
 * @param event the event
 */
  onArticleContentClick(event: MouseEvent): void {
    const target = event.target instanceof Element ? event.target : null;
    const copyButton = target?.closest('.article-code-copy') as HTMLElement | null;
    if (!copyButton) return;
    const pre = copyButton.closest('pre');
    const code = pre?.querySelector('code')?.textContent ?? '';
    this.copyCodeToClipboard(copyButton, code);
  }

/**
 * Performs the copy code to clipboard operation.
 * @param button the button
 * @param value the value
 * @returns the operation result
 */
  private async copyCodeToClipboard(button: HTMLElement, value: string): Promise<void> {
    if (!value.trim()) {
      return;
    }

    const showTemporaryLabel = (label: 'Copied' | 'Failed') => {
      const previous = button.textContent;
      button.textContent = label;
      setTimeout(() => {
        if (previous === 'Copied' || previous === 'Failed') {
          button.textContent = 'Copy';
          return;
        }
        button.textContent = previous;
      }, 1200);
    };

    if (!navigator.clipboard?.writeText || !window.isSecureContext) {
      showTemporaryLabel('Failed');
      return;
    }

    try {
      await navigator.clipboard.writeText(value);
      showTemporaryLabel('Copied');
    } catch {
      showTemporaryLabel('Failed');
    }
  }

/**
 * Performs the format likes count operation.
 * @param value the value
 * @returns the operation result
 */
  private formatLikesCount(value: number): string {
    return new Intl.NumberFormat(undefined, {
      notation: 'compact',
      compactDisplay: 'short',
      maximumFractionDigits: 1,
    }).format(value);
  }

}
