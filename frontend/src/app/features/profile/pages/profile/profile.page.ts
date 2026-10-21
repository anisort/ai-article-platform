import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {Component, computed, inject, OnInit, signal} from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ModalController } from '@ionic/angular/standalone';
import { AuthService } from '../../../../core/services/auth.service';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import { ArticlePreview } from '../../../articles/models/article-preview.model';
import { ArticlePreviewCardComponent } from '../../../articles/components/article-preview-card/article-preview-card.component';
import { ProfileConnectionsModalComponent } from '../../components/profile-connections-modal/profile-connections-modal.component';
import { ProfileSummaryCardComponent } from '../../components/profile-summary-card/profile-summary-card.component';
import { UserProfile } from '../../models/user-profile.model';
import { ProfileService } from '../../services/profile.service';
import { ArticlesService } from '../../../articles/services/articles.service';
import {
  SegmentedControlComponent,
  SegmentedControlOption
} from '../../../../shared/components/segmented-control/segmented-control.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

/**
 * Defines the profile tab type.
 */
type ProfileTab = 'published' | 'drafts' | 'liked';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageIntroComponent,
    ArticlePreviewCardComponent,
    ProfileSummaryCardComponent,
    SegmentedControlComponent,
    LoadingStateComponent
  ],
  templateUrl: './profile.page.html',
  styleUrl: './profile.page.scss',
})
/**
 * Defines the profile page class.
 */
export class ProfilePage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly profileService = inject(ProfileService);
  private readonly articlesService = inject(ArticlesService);
  private readonly modalController = inject(ModalController);

  readonly activeTab = signal<ProfileTab>('published');
  readonly loading = signal(false);
  readonly followLoading = signal(false);
  readonly deletingArticleId = signal<string | null>(null);

  readonly profile = signal<UserProfile | null>(null);
  readonly articles = signal<ArticlePreview[]>([]);
  readonly likedArticles = signal<ArticlePreview[]>([]);
  readonly publishedArticlesByAuthor = signal<ArticlePreview[]>([]);

  readonly publishedArticles = computed(() =>
    this.articles().filter(article => article.published)
  );

  readonly draftArticles = computed(() =>
    this.articles().filter(article => !article.published)
  );

  readonly visibleArticlePreviews = computed(() =>
    this.isOwnProfile() ? this.visibleArticles() : this.publishedArticlesByAuthor()
  );

  readonly tabOptions = computed<SegmentedControlOption<ProfileTab>[]>(() => {
    const base: SegmentedControlOption<ProfileTab>[] = [
      { value: 'published', label: 'Published', count: this.tabCount('published') },
    ];

    if (this.isOwnProfile()) {
      base.push(
        { value: 'drafts', label: 'Drafts', count: this.tabCount('drafts') },
        { value: 'liked', label: 'Liked', count: this.tabCount('liked') },
      );
    }

    return base;
  });

/**
 * Performs the ng on init operation.
 */
  ngOnInit(): void {
    this.loadProfile();
  }

/**
 * Performs the set tab operation.
 * @param tab the tab
 */
  setTab(tab: ProfileTab): void {
    this.activeTab.set(tab);
  }

/**
 * Performs the visible articles operation.
 * @returns the operation result
 */
  visibleArticles(): ArticlePreview[] {
    switch (this.activeTab()) {
      case 'published':
        return this.publishedArticles();
      case 'drafts':
        return this.draftArticles();
      default:
        return this.likedArticles();
    }
  }

/**
 * Performs the tab count operation.
 * @param tab the tab
 * @returns the operation result
 */
  tabCount(tab: ProfileTab): number {
    switch (tab) {
      case 'published':
        return this.isOwnProfile() ? this.publishedArticles().length : this.publishedArticlesByAuthor().length;
      case 'drafts':
        return this.draftArticles().length;
      case 'liked':
        return this.likedArticles().length;
    }
  }

/**
 * Performs the profile title operation.
 * @returns the operation result
 */
  profileTitle(): string {
    const profile = this.profile();
    if (!profile) {
      return 'Profile';
    }

    return profile.ownProfile ? 'Your profile' : `${this.displayName()} profile`;
  }

/**
 * Performs the profile copy operation.
 * @returns the operation result
 */
  profileCopy(): string {
    const profile = this.profile();
    if (!profile) {
      return 'Published articles, drafts, likes and social connections in one place.';
    }

    return profile.ownProfile
      ? 'Manage your published articles, drafts, likes and subscriptions.'
      : 'Published articles and social connections of this author.';
  }

/**
 * Performs the display name operation.
 * @returns the operation result
 */
  displayName(): string {
    const profile = this.profile();
    return profile?.name || profile?.username || 'Unknown author';
  }

/**
 * Performs the open connections modal operation.
 * @param tab the tab
 * @returns the operation result
 */
  async openConnectionsModal(tab: 'following' | 'followers'): Promise<void> {
    const modal = await this.modalController.create({
      component: ProfileConnectionsModalComponent,
      componentProps: {
        tab,
        users: tab === 'following'
          ? this.profile()?.followingUsers ?? []
          : this.profile()?.followers ?? [],
      },
      cssClass: 'profile-connections-ionic-modal',
    });

    await modal.present();
  }

/**
 * Performs the is own profile operation.
 * @returns the operation result
 */
  isOwnProfile(): boolean {
    return this.profile()?.ownProfile ?? false;
  }

/**
 * Performs the can follow operation.
 * @returns the operation result
 */
  canFollow(): boolean {
    const profile = this.profile();
    return !!profile && !profile.ownProfile && this.authService.isAuthenticated();
  }

/**
 * Performs the toggle follow operation.
 */
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

/**
 * Performs the on delete article operation.
 * @param articleId the articleId
 */
  onDeleteArticle(articleId: string): void {
    if (!articleId || !this.isOwnProfile() || this.deletingArticleId()) {
      return;
    }

    const shouldDelete = window.confirm('Delete this article? This action cannot be undone.');
    if (!shouldDelete) {
      return;
    }

    this.deletingArticleId.set(articleId);
    this.articlesService.delete(articleId).subscribe({
      next: () => {
        this.articles.update(items => items.filter(article => article.id !== articleId));
        this.deletingArticleId.set(null);
      },
      error: () => {
        this.deletingArticleId.set(null);
      },
    });
  }

/**
 * Performs the load profile operation.
 */
  private loadProfile(): void {
    const userId = this.route.snapshot.paramMap.get('id');
    const currentUserId = this.authService.userId;

    if (!userId) {
      this.loadOwnProfile();
      return;
    }

    if (currentUserId && currentUserId === userId) {
      this.loadOwnProfile();
      return;
    }

    this.loadForeignProfile(userId);
  }

/**
 * Performs the load own profile operation.
 */
  private loadOwnProfile(): void {
    this.loading.set(true);

    this.profileService.getProfilePage().subscribe({
      next: response => {
        this.profile.set(response.profile);
        this.articles.set(response.ownArticles);
        this.likedArticles.set(response.likedArticles);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        if (error.status === 404 || error.status === 400) {
          this.router.navigate(['/not-found']);
        }
      },
    });
  }

/**
 * Performs the load foreign profile operation.
 * @param userId the userId
 */
  private loadForeignProfile(userId: string): void {
    this.loading.set(true);

    this.profileService.getProfilePage(userId).subscribe({
      next: response => {
        this.profile.set(response.profile);
        this.publishedArticlesByAuthor.set(response.publishedArticles);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        if (error.status === 404 || error.status === 400) {
          this.router.navigate(['/not-found']);
        }
      },
    });
  }
}
