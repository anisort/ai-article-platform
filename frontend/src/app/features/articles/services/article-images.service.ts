import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ArticleImage } from '../models/article-image.model';

@Injectable({
  providedIn: 'root'
})
/**
 * Defines the article images service class.
 */
export class ArticleImagesService {
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;
  private readonly http = inject(HttpClient);

/**
 * Performs the add operation.
 * @param articleId the articleId
 * @param imageUrl the imageUrl
 * @returns the operation result
 */
  add(articleId: string, imageUrl: string): Observable<ArticleImage> {
    return this.http.post<ArticleImage>(`${this.apiUrl}/${articleId}/images`, { imageUrl });
  }

/**
 * Performs the delete operation.
 * @param articleId the articleId
 * @param imageId the imageId
 * @returns the operation result
 */
  delete(articleId: string, imageId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${articleId}/images/${imageId}`);
  }
}
