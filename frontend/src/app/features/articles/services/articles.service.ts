import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CreateArticleRequest } from '../models/create-article-request.model';
import { ArticleWithUserResponse } from '../models/article-response.model';
import { ArticlePreview } from '../models/article-preview.model';
import { EditArticlePageResponse } from '../models/edit-article-page-response.model';

/**
 * Defines the search mode type.
 */
export type SearchMode = 'title' | 'semantic';

@Injectable({
  providedIn: 'root'
})
/**
 * Defines the articles service class.
 */
export class ArticlesService {
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;
  private readonly http = inject(HttpClient);

/**
 * Performs the list operation.
 * @returns the operation result
 */
  list(): Observable<ArticlePreview[]> {
    return this.http.get<ArticlePreview[]>(this.apiUrl);
  }

/**
 * Performs the search operation.
 * @param query the query
 * @param mode the mode
 * @returns the operation result
 */
  search(query: string, mode: SearchMode): Observable<ArticlePreview[]> {
    return this.http.get<ArticlePreview[]>(
      `${environment.apiUrlArticles}/v1/search/articles`,
      {
        params: { query, mode },
      },
    );
  }

/**
 * Performs the get recommendations operation.
 * @param limit the limit
 * @returns the operation result
 */
  getRecommendations(limit = 20): Observable<ArticlePreview[]> {
    return this.http.get<ArticlePreview[]>(`${this.apiUrl}/recommendations`, {
      params: { limit },
    });
  }

/**
 * Performs the get by id operation.
 * @param articleId the articleId
 * @returns the operation result
 */
  getById(articleId: string): Observable<ArticleWithUserResponse> {
    return this.http.get<ArticleWithUserResponse>(`${this.apiUrl}/${articleId}`);
  }

/**
 * Performs the get edit page operation.
 * @param articleId the articleId
 * @returns the operation result
 */
  getEditPage(articleId: string): Observable<EditArticlePageResponse> {
    return this.http.get<EditArticlePageResponse>(`${this.apiUrl}/${articleId}/edit-page`);
  }

/**
 * Performs the create operation.
 * @param article the article
 * @returns the operation result
 */
  create(article: CreateArticleRequest): Observable<ArticleWithUserResponse> {
    return this.http.post<ArticleWithUserResponse>(this.apiUrl, article);
  }

/**
 * Performs the update operation.
 * @param articleId the articleId
 * @param article the article
 * @returns the operation result
 */
  update(articleId: string, article: CreateArticleRequest): Observable<ArticleWithUserResponse> {
    return this.http.put<ArticleWithUserResponse>(`${this.apiUrl}/${articleId}`, article);
  }

/**
 * Performs the delete operation.
 * @param articleId the articleId
 * @returns the operation result
 */
  delete(articleId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${articleId}`);
  }
}
