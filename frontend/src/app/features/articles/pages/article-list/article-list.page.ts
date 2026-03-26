import {Component, inject, OnInit, signal} from '@angular/core';
import {AuthService} from '../../../../core/services/auth.service';
import {ArticleWithUserResponse} from '../../models/article-response.model';
import {ArticlesService} from '../../services/articles.service';

@Component({
  selector: 'app-article-list',
  imports: [],
  templateUrl: './article-list.page.html',
  styleUrl: './article-list.page.scss'
})
export class ArticleListPage implements OnInit {
  articlesService = inject(ArticlesService);

  articles = signal<ArticleWithUserResponse[]>([]);

  ngOnInit(): void {
    this.loadArticles();
    console.log('articles:', this.articles());
  }

  loadArticles() {
    this.articlesService.list().subscribe(articles => this.articles.set(articles));
  }
}
