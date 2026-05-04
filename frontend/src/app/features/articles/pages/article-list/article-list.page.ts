import {CommonModule} from '@angular/common';
import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Router, RouterLink} from '@angular/router';
import {ArticlesService, SearchMode} from '../../services/articles.service';
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
  route = inject(ActivatedRoute);
  router = inject(Router);

  articles = signal<PublishedArticlePreview[]>([]);
  searchMode = signal<SearchMode>('semantic');
  query = signal('');

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const query = params.get('q') ?? '';
      const mode = (params.get('mode') === 'title' ? 'title' : 'semantic') as SearchMode;
      this.query.set(query);
      this.searchMode.set(mode);
      this.loadArticles(query, mode);
    });
  }

  loadArticles(query = '', mode: SearchMode = 'semantic') {
    this.articlesService.search(query, mode).subscribe(articles => this.articles.set(articles));
  }

  onModeChange(mode: SearchMode) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        mode,
      },
      queryParamsHandling: 'merge',
    });
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
