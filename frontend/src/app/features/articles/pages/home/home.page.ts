import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ArticlePreview } from '../../models/article-preview.model';
import { ArticlesService } from '../../services/articles.service';
import { ArticlePreviewCardComponent } from '../../components/article-preview-card/article-preview-card.component';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, RouterLink, ArticlePreviewCardComponent, PageIntroComponent, LoadingStateComponent],
  templateUrl: './home.page.html',
  styleUrl: './home.page.scss'
})
export class HomePage implements OnInit {
  private readonly articlesService = inject(ArticlesService);

  readonly recommendations = signal<ArticlePreview[]>([]);
  readonly loading = signal(true);
  readonly carouselArticles = computed(() => this.recommendations().slice(0, 8));
  readonly feedArticles = computed(() => this.recommendations().slice(8, 20));

  ngOnInit(): void {
    this.articlesService.getRecommendations(20).subscribe({
      next: items => {
        this.recommendations.set(items);
        this.loading.set(false);
      },
      error: () => {
        this.recommendations.set([]);
        this.loading.set(false);
      },
    });
  }

}
