import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { ArticleWithUserResponse } from '../../articles/models/article-response.model';

export interface LikedArticlesResponse {
  articleIds: string[];
}

export interface ToggleArticleLikeResponse {
  articleId: string;
  liked: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MyArticlesService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;

  listMine(): Observable<ArticleWithUserResponse[]> {
    return this.http.get<ArticleWithUserResponse[]>(`${this.apiUrl}/me`);
  }

  listLikes(): Observable<LikedArticlesResponse> {
    return this.http.get<LikedArticlesResponse>(`${this.apiUrl}/me/likes`);
  }

  toggleLike(articleId: string): Observable<ToggleArticleLikeResponse> {
    return this.http.post<ToggleArticleLikeResponse>(`${this.apiUrl}/me/${articleId}/like`, {});
  }
}
