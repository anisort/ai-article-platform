import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ArticleImage } from '../models/article-image.model';

@Injectable({
  providedIn: 'root'
})
export class ArticleImagesService {
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;
  private readonly http = inject(HttpClient);

  list(articleId: string): Observable<ArticleImage[]> {
    return this.http.get<ArticleImage[]>(`${this.apiUrl}/${articleId}/images`);
  }

  add(articleId: string, imageUrl: string): Observable<ArticleImage> {
    return this.http.post<ArticleImage>(`${this.apiUrl}/${articleId}/images`, { imageUrl });
  }

  delete(articleId: string, imageId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${articleId}/images/${imageId}`);
  }
}
