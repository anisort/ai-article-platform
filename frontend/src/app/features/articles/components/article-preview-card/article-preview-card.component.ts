import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ArticlePreview } from '../../models/article-preview.model';

@Component({
  selector: 'app-article-preview-card',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './article-preview-card.component.html',
  styleUrl: './article-preview-card.component.scss',
})
/**
 * Defines the article preview card component class.
 */
export class ArticlePreviewCardComponent {
  article = input.required<ArticlePreview>();
  size = input<'small' | 'large'>('large');
  showOwnerActions = input(false);
  deleteLoading = input(false);
  deleteArticle = output<string>();

  authorName = computed(() => {
    const author = this.article().author;
    return author?.name || author?.username || 'Unknown author';
  });

/**
 * Performs the on delete click operation.
 */
  onDeleteClick(): void {
    this.deleteArticle.emit(this.article().id);
  }
}
