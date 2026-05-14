import {CommonModule} from '@angular/common';
import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Router} from '@angular/router';
import {ArticlesService, SearchMode} from '../../services/articles.service';
import {ArticlePreview} from '../../models/article-preview.model';
import { ArticlePreviewCardComponent } from '../../components/article-preview-card/article-preview-card.component';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import {
  SegmentedControlComponent,
  SegmentedControlOption
} from '../../../../shared/components/segmented-control/segmented-control.component';

@Component({
  selector: 'app-article-list',
  standalone: true,
  imports: [CommonModule, ArticlePreviewCardComponent, PageIntroComponent, SegmentedControlComponent],
  templateUrl: './article-list.page.html',
  styleUrl: './article-list.page.scss'
})
export class ArticleListPage implements OnInit {
  articlesService = inject(ArticlesService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  articles = signal<ArticlePreview[]>([]);
  searchMode = signal<SearchMode>('semantic');
  query = signal('');
  modeOptions: SegmentedControlOption<SearchMode>[] = [
    { value: 'semantic', label: 'Semantic' },
    { value: 'title', label: 'Title' },
  ];

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const query = params.get('q') ?? '';
      const rawMode = params.get('mode');
      const mode = (rawMode === 'title' ? 'title' : 'semantic') as SearchMode;

      if (!rawMode) {
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: { mode: 'semantic' },
          queryParamsHandling: 'merge',
          replaceUrl: true,
        });
        return;
      }

      this.query.set(query);
      this.searchMode.set(mode);
      this.loadArticles(query, mode);
    });
  }

  loadArticles(query = '', mode: SearchMode = 'semantic') {
    const normalizedQuery = query.trim();
    const request$ = normalizedQuery
      ? this.articlesService.search(normalizedQuery, mode)
      : this.articlesService.list();

    request$.subscribe(articles => this.articles.set(articles));
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
}
