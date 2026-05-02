import {CommonModule} from '@angular/common';
import {Component, inject, OnInit, signal} from '@angular/core';
import {RouterLink} from '@angular/router';
import {ArticlesService} from '../../services/articles.service';
import {PublishedArticlePreview} from '../../models/published-article-preview.model';

@Component({
  selector: 'app-article-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './article-list.page.html',
  styleUrl: './article-list.page.scss'
})
export class ArticleListPage implements OnInit {
  articlesService = inject(ArticlesService);

  articles = signal<PublishedArticlePreview[]>([]);

  ngOnInit(): void {
    this.loadArticles();
  }

  loadArticles() {
    this.articlesService.list().subscribe(articles => this.articles.set(articles));
  }

  authorName(article: PublishedArticlePreview): string {
    return article.author?.name || article.author?.username || 'Unknown author';
  }

  cardClass(index: number): string {
    const variants = [
      'article-card--tall',
      'article-card--square',
      'article-card--wide',
      'article-card--wide-soft',
      'article-card--hero',
      'article-card--square-tall',
    ];

    return variants[index % variants.length];
  }
}
