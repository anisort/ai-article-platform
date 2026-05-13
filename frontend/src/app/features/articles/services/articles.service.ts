import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CreateArticleRequest } from '../models/create-article-request.model';
import { ArticleWithUserResponse } from '../models/article-response.model';
import { PublishedArticlePreview } from '../models/published-article-preview.model';

export type SearchMode = 'title' | 'semantic';

@Injectable({
  providedIn: 'root'
})
export class ArticlesService {
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;
  private readonly http = inject(HttpClient);

  list(): Observable<PublishedArticlePreview[]> {
    return this.http.get<PublishedArticlePreview[]>(this.apiUrl);
  }

  search(query: string, mode: SearchMode): Observable<PublishedArticlePreview[]> {
    return this.http.get<PublishedArticlePreview[]>(
      `${environment.apiUrlArticles}/v1/search/articles`,
      {
        params: { query, mode },
      },
    );
  }

  listPublishedByAuthor(authorId: string): Observable<PublishedArticlePreview[]> {
    return this.http.get<PublishedArticlePreview[]>(`${this.apiUrl}/author/${authorId}`);
  }

  getRecommendations(limit = 20): Observable<PublishedArticlePreview[]> {
    return this.http.get<PublishedArticlePreview[]>(`${this.apiUrl}/recommendations`, {
      params: { limit },
    });
  }

  getById(articleId: string): Observable<ArticleWithUserResponse> {
    return this.http.get<ArticleWithUserResponse>(`${this.apiUrl}/${articleId}`);
  }

  create(article: CreateArticleRequest): Observable<ArticleWithUserResponse> {
    return this.http.post<ArticleWithUserResponse>(this.apiUrl, article);
  }

  update(articleId: string, article: CreateArticleRequest): Observable<ArticleWithUserResponse> {
    return this.http.put<ArticleWithUserResponse>(`${this.apiUrl}/${articleId}`, article);
  }
}
