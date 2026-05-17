import { CommonModule, DatePipe } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnInit, Renderer2, ViewChild, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MarkdownModule } from 'ngx-markdown';
import { AuthService } from '../../../../core/services/auth.service';
import { ArticlesService } from '../../services/articles.service';
import { ArticleWithUserResponse } from '../../models/article-response.model';
import { MyArticlesService } from '../../../profile/services/my-articles.service';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import { ProfileSummaryCardComponent } from '../../../profile/components/profile-summary-card/profile-summary-card.component';
import { UserProfile } from '../../../profile/models/user-profile.model';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-article-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink, MarkdownModule, DatePipe, PageIntroComponent, ProfileSummaryCardComponent, LoadingStateComponent],
  templateUrl: './article-detail.page.html',
  styleUrl: './article-detail.page.scss',
})
export class ArticleDetailPage implements OnInit, AfterViewInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly articlesService = inject(ArticlesService);
  private readonly authService = inject(AuthService);
  private readonly myArticlesService = inject(MyArticlesService);
  private readonly renderer = inject(Renderer2);

  @ViewChild('markdownHost') private markdownHost?: ElementRef<HTMLElement>;

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

  ngAfterViewInit(): void {
    this.scheduleCodeBlockEnhancement();
  }

  onMarkdownReady(): void {
    this.scheduleCodeBlockEnhancement();
  }

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
        this.scheduleCodeBlockEnhancement();
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        if (error.status === 404) {
          this.router.navigate(['/not-found']);
        }
      },
    });
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
        this.likesCount.set(response.likesCount);
        this.likeLoading.set(false);
      },
      error: () => {
        this.likeLoading.set(false);
      },
    });
  }

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

  contentMarkdown(): string {
    const article = this.articleData();
    if (!article) {
      return '';
    }
    return article.markdownContent ?? '';
  }

  private scheduleCodeBlockEnhancement(): void {
    setTimeout(() => this.enhanceCodeBlocks(), 0);
  }

  private enhanceCodeBlocks(): void {
    const root = this.markdownHost?.nativeElement;
    if (!root) {
      return;
    }

    const blocks = Array.from(root.querySelectorAll('pre'));
    for (const pre of blocks) {
      if (pre.dataset['enhanced'] === 'true') {
        continue;
      }

      const code = pre.querySelector('code');
      if (!code) {
        continue;
      }

      const language = this.detectCodeLanguage(code.className);
      const toolbar = this.renderer.createElement('div');
      toolbar.className = 'article-code-toolbar';

      const languageLabel = this.renderer.createElement('span');
      languageLabel.className = 'article-code-language';
      languageLabel.textContent = language;

      const copyButton = this.renderer.createElement('button');
      copyButton.className = 'article-code-copy';
      copyButton.type = 'button';
      copyButton.textContent = 'Copy';
      this.renderer.listen(copyButton, 'click', () => {
        this.copyCodeToClipboard(copyButton, code.textContent ?? '');
      });

      this.renderer.appendChild(toolbar, languageLabel);
      this.renderer.appendChild(toolbar, copyButton);
      this.renderer.insertBefore(pre, toolbar, pre.firstChild);
      pre.dataset['enhanced'] = 'true';
    }
  }

  private detectCodeLanguage(className: string): string {
    const classTokens = className.split(' ').map(token => token.trim()).filter(Boolean);
    const token = classTokens.find(value => value.startsWith('language-') || value.startsWith('lang-'));
    if (!token) {
      return 'text';
    }
    return token.replace('language-', '').replace('lang-', '') || 'text';
  }

  private async copyCodeToClipboard(button: HTMLButtonElement, value: string): Promise<void> {
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

  private formatLikesCount(value: number): string {
    return new Intl.NumberFormat(undefined, {
      notation: 'compact',
      compactDisplay: 'short',
      maximumFractionDigits: 1,
    }).format(value);
  }

}
