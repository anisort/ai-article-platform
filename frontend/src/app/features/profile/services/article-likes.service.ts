import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';

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
export class ArticleLikesService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;

  list(): Observable<LikedArticlesResponse> {
    return this.http.get<LikedArticlesResponse>(`${this.apiUrl}/me/likes`);
  }

  toggle(articleId: string): Observable<ToggleArticleLikeResponse> {
    return this.http.post<ToggleArticleLikeResponse>(`${this.apiUrl}/${articleId}/like`, {});
  }
}
