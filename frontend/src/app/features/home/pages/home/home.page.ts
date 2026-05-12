import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PublishedArticlePreview } from '../../../articles/models/published-article-preview.model';
import { ArticlesService } from '../../../articles/services/articles.service';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.page.html',
  styleUrl: './home.page.scss'
})
export class HomePage implements OnInit {
  private readonly articlesService = inject(ArticlesService);

  readonly recommendations = signal<PublishedArticlePreview[]>([]);
  readonly loading = signal(true);

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

  authorName(article: PublishedArticlePreview): string {
    return article.author?.name || article.author?.username || 'Unknown author';
  }
}

