import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PublishedArticlePreview } from '../../models/published-article-preview.model';

@Component({
  selector: 'app-article-preview-card',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './article-preview-card.component.html',
  styleUrl: './article-preview-card.component.scss',
})
export class ArticlePreviewCardComponent {
  article = input.required<PublishedArticlePreview>();
  size = input<'small' | 'large'>('large');

  authorName = computed(() => {
    const author = this.article().author;
    return author?.name || author?.username || 'Unknown author';
  });
}
