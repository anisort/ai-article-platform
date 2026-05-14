import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CreateArticleRequest } from '../models/create-article-request.model';
import { ArticleWithUserResponse } from '../models/article-response.model';
import { ArticlePreview } from '../models/article-preview.model';

export type SearchMode = 'title' | 'semantic';

@Injectable({
  providedIn: 'root'
})
export class ArticlesService {
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;
  private readonly http = inject(HttpClient);

  list(): Observable<ArticlePreview[]> {
    return this.http.get<ArticlePreview[]>(this.apiUrl);
  }

  search(query: string, mode: SearchMode): Observable<ArticlePreview[]> {
    return this.http.get<ArticlePreview[]>(
      `${environment.apiUrlArticles}/v1/search/articles`,
      {
        params: { query, mode },
      },
    );
  }

  getRecommendations(limit = 20): Observable<ArticlePreview[]> {
    return this.http.get<ArticlePreview[]>(`${this.apiUrl}/recommendations`, {
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
