import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {CreateArticleRequest} from '../models/create-article-request.model';
import {Observable} from 'rxjs';
import {ArticleWithUserResponse} from '../models/article-response.model';

@Injectable({
  providedIn: 'root'
})
export class ArticlesService {
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;
  private readonly http = inject(HttpClient);

  list() {
    return this.http.get<ArticleWithUserResponse[]>(this.apiUrl);
  }

  create(article: CreateArticleRequest): Observable<ArticleWithUserResponse> {
    return this.http.post<ArticleWithUserResponse>(this.apiUrl, article);
  }
}
