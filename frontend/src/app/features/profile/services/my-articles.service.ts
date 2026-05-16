import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';

export interface ToggleArticleLikeResponse {
  articleId: string;
  liked: boolean;
  likesCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class MyArticlesService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;

  toggleLike(articleId: string): Observable<ToggleArticleLikeResponse> {
    return this.http.post<ToggleArticleLikeResponse>(`${this.apiUrl}/me/${articleId}/like`, {});
  }
}
