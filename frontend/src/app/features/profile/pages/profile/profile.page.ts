import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';

import { AuthService } from '../../../../core/services/auth.service';
import { ArticleWithUserResponse } from '../../../articles/models/article-response.model';
import { PublishedArticlePreview } from '../../../articles/models/published-article-preview.model';
import { ArticlesService } from '../../../articles/services/articles.service';
import { UserProfile, UserSummary } from '../../models/user-profile.model';
import { MyArticlesService } from '../../services/my-articles.service';
import { ProfileService } from '../../services/profile.service';
import {
  ProfileArticleCardComponent,
  ProfileArticleCardData
} from '../../components/profile-article-card/profile-article-card.component';
import { ProfileConnectionCardComponent } from '../../components/profile-connection-card/profile-connection-card.component';
import { ProfileSummaryCardComponent } from '../../components/profile-summary-card/profile-summary-card.component';

type ProfileTab = 'published' | 'drafts' | 'liked';
type ConnectionTab = 'following' | 'followers' | null;

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ProfileSummaryCardComponent,
    ProfileArticleCardComponent,
    ProfileConnectionCardComponent
  ],
  templateUrl: './profile.page.html',
  styleUrl: './profile.page.scss',
})
export class ProfilePage {
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly articlesService = inject(ArticlesService);
  private readonly myArticlesService = inject(MyArticlesService);
  private readonly profileService = inject(ProfileService);

  readonly activeTab = signal<ProfileTab>('published');
  readonly loading = signal(false);
  readonly unauthorized = signal(false);
  readonly notFound = signal(false);
  readonly followLoading = signal(false);
  readonly activeConnectionTab = signal<ConnectionTab>(null);

  readonly profile = signal<UserProfile | null>(null);
  readonly articles = signal<ArticleWithUserResponse[]>([]);
  readonly likedArticles = signal<ArticleWithUserResponse[]>([]);
  readonly publishedArticlesByAuthor = signal<PublishedArticlePreview[]>([]);

  readonly publishedArticles = computed(() =>
    this.articles().filter(article => article.articleResponse.published)
  );

  readonly draftArticles = computed(() =>
    this.articles().filter(article => !article.articleResponse.published)
  );

  readonly ownArticleCards = computed(() =>
    this.visibleArticles().map(article => this.toOwnArticleCard(article))
  );

  readonly publishedArticleCards = computed(() =>
    this.visiblePublishedPreviews().map(article => this.toPublishedArticleCard(article))
  );

  constructor() {
    this.loadProfile();
  }

  setTab(tab: ProfileTab): void {
    this.activeTab.set(tab);
  }

  visibleArticles(): ArticleWithUserResponse[] {
    if (this.activeTab() === 'published') {
      return this.publishedArticles();
    }

    if (this.activeTab() === 'drafts') {
      return this.draftArticles();
    }

    return this.likedArticles();
  }

  visiblePublishedPreviews(): PublishedArticlePreview[] {
    return this.publishedArticlesByAuthor();
  }

  tabCount(tab: ProfileTab): number {
    if (tab === 'published') {
      return this.isOwnProfile() ? this.publishedArticles().length : this.publishedArticlesByAuthor().length;
    }

    if (tab === 'drafts') {
      return this.draftArticles().length;
    }

    if (tab === 'liked') {
      return this.likedArticles().length;
    }

    return 0;
  }

  profileTitle(): string {
    const profile = this.profile();
    if (!profile) {
      return 'Profile';
    }

    return profile.ownProfile ? 'Your profile' : `${this.displayName()} profile`;
  }

  profileCopy(): string {
    const profile = this.profile();
    if (!profile) {
      return '';
    }

    return profile.ownProfile
      ? 'Manage your published articles, drafts, likes and subscriptions.'
      : 'Only published articles are visible on someone else`s profile.';
  }

  displayName(): string {
    const profile = this.profile();
    return profile?.name || profile?.username || 'Unknown author';
  }

  profileHandle(): string {
    const username = this.profile()?.username;
    return username ? `@${username}` : '';
  }

  followersCount(): number {
    return this.profile()?.followers.length ?? 0;
  }

  followingCount(): number {
    return this.profile()?.followingUsers.length ?? 0;
  }

  setConnectionTab(tab: Exclude<ConnectionTab, null>): void {
    this.activeConnectionTab.update(current => current === tab ? null : tab);
  }

  connectionTab(): ConnectionTab {
    return this.activeConnectionTab();
  }

  closeConnectionTab(): void {
    this.activeConnectionTab.set(null);
  }

  showArticlesSection(): boolean {
    return true;
  }

  modalTitle(): string {
    return this.connectionTab() === 'following' ? 'Following' : 'Followers';
  }

  modalUsers(): UserSummary[] {
    if (this.connectionTab() === 'following') {
      return this.profile()?.followingUsers ?? [];
    }

    if (this.connectionTab() === 'followers') {
      return this.profile()?.followers ?? [];
    }

    return [];
  }

  isOwnProfile(): boolean {
    return this.profile()?.ownProfile ?? false;
  }

  canFollow(): boolean {
    const profile = this.profile();
    return !!profile && !profile.ownProfile && this.authService.isAuthenticated();
  }

  toggleFollow(): void {
    const profile = this.profile();
    if (!profile || !this.canFollow() || this.followLoading()) {
      return;
    }

    this.followLoading.set(true);
    this.profileService.toggleFollow(profile.id).subscribe({
      next: response => {
        this.profile.update(current => current ? { ...current, following: response.following } : current);
        this.followLoading.set(false);
      },
      error: () => {
        this.followLoading.set(false);
      },
    });
  }

  private toOwnArticleCard(article: ArticleWithUserResponse): ProfileArticleCardData {
    return {
      id: article.articleResponse.id,
      title: article.articleResponse.title,
      imageUrl: article.articleResponse.imageUrl ?? '',
      summary: article.articleResponse.summary || 'No summary yet.',
      dateLabel: article.articleResponse.createdAt,
      articleLink: ['/articles', article.articleResponse.id],
      editLink: ['/edit-article', article.articleResponse.id],
    };
  }

  private toPublishedArticleCard(article: PublishedArticlePreview): ProfileArticleCardData {
    return {
      id: article.id,
      title: article.title,
      imageUrl: article.imageUrl ?? '',
      dateLabel: 'Published article',
      articleLink: ['/articles', article.id],
    };
  }

  private loadProfile(): void {
    const userId = this.route.snapshot.paramMap.get('id');
    const currentUserId = this.authService.userId;

    if (!userId) {
      if (!this.authService.isAuthenticated()) {
        this.unauthorized.set(true);
        return;
      }

      this.loadOwnProfile();
      return;
    }

    if (currentUserId && currentUserId === userId) {
      if (!this.authService.isAuthenticated()) {
        this.unauthorized.set(true);
        return;
      }

      this.loadOwnProfile();
      return;
    }

    this.loadForeignProfile(userId);
  }

  private loadOwnProfile(): void {
    this.loading.set(true);

    forkJoin({
      profile: this.profileService.me(),
      ownArticles: this.myArticlesService.listMine(),
      likedArticles: this.loadLikedArticles(),
    }).subscribe({
      next: ({ profile, ownArticles, likedArticles }) => {
        this.profile.set(profile);
        this.articles.set(ownArticles);
        this.likedArticles.set(likedArticles);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.unauthorized.set(error.status === 401 || error.status === 403);
      },
    });
  }

  private loadForeignProfile(userId: string): void {
    this.loading.set(true);

    forkJoin({
      profile: this.profileService.getById(userId),
      publishedArticles: this.articlesService.listPublishedByAuthor(userId),
    }).subscribe({
      next: ({ profile, publishedArticles }) => {
        this.profile.set(profile);
        this.publishedArticlesByAuthor.set(publishedArticles);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.unauthorized.set(error.status === 401 || error.status === 403);
        this.notFound.set(error.status === 404);
      },
    });
  }

  private loadLikedArticles() {
    return this.myArticlesService.listLikes().pipe(
      map(response => response.articleIds.filter((articleId, index, ids) => ids.indexOf(articleId) === index)),
      switchMap(articleIds => {
        if (!articleIds.length) {
          return of([] as ArticleWithUserResponse[]);
        }

        return forkJoin(
          articleIds.map(articleId =>
            this.articlesService.getById(articleId).pipe(
              catchError(() => of(null))
            )
          )
        ).pipe(
          map(articles => articles.filter((article): article is ArticleWithUserResponse => article !== null))
        );
      }),
      catchError(() => of([] as ArticleWithUserResponse[]))
    );
  }
}
