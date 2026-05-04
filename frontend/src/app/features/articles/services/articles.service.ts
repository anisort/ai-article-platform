import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { CreateArticleRequest } from '../models/create-article-request.model';
import { map, Observable } from 'rxjs';
import { ArticleBlockResponse } from '../models/article-block-response.model';
import { ArticleWithUserResponse } from '../models/article-response.model';
import { PublishedArticlePreview } from '../models/published-article-preview.model';

@Injectable({
  providedIn: 'root'
})
export class ArticlesService {
  private readonly apiUrl = `${environment.apiUrlArticles}/v1/articles`;
  private readonly http = inject(HttpClient);

  list(): Observable<PublishedArticlePreview[]> {
    return this.http
      .get<Array<PublishedArticlePreview | LegacyArticleWithUserResponse>>(this.apiUrl)
      .pipe(map(items => items.map(item => this.toPublishedPreview(item))));
  }

  search(query: string, mode: SearchMode): Observable<PublishedArticlePreview[]> {
    const normalizedQuery = query.trim();
    if (!normalizedQuery) {
      return this.list();
    }

    return this.http.get<PublishedArticlePreview[]>(`${this.apiUrl}/search`, {
      params: {
        query: normalizedQuery,
        mode,
      },
    });
  }

  listMine(): Observable<ArticleWithUserResponse[]> {
    return this.http
      .get<ArticleWithUserResponse[]>(`${this.apiUrl}/me`)
      .pipe(map(items => items.map(item => this.normalizeArticle(item))));
  }

  listPublishedByAuthor(authorId: string): Observable<PublishedArticlePreview[]> {
    return this.http.get<PublishedArticlePreview[]>(`${this.apiUrl}/author/${authorId}`);
  }

  getById(articleId: string): Observable<ArticleWithUserResponse> {
    return this.http
      .get<ArticleWithUserResponse>(`${this.apiUrl}/${articleId}`)
      .pipe(map(article => this.normalizeArticle(article)));
  }

  create(article: CreateArticleRequest): Observable<ArticleWithUserResponse> {
    return this.http
      .post<ArticleWithUserResponse>(this.apiUrl, article)
      .pipe(map(response => this.normalizeArticle(response)));
  }

  update(articleId: string, article: CreateArticleRequest): Observable<ArticleWithUserResponse> {
    return this.http
      .put<ArticleWithUserResponse>(`${this.apiUrl}/${articleId}`, article)
      .pipe(map(response => this.normalizeArticle(response)));
  }

  private toPublishedPreview(
    item: PublishedArticlePreview | LegacyArticleWithUserResponse
  ): PublishedArticlePreview {
    if ('articleResponse' in item) {
      return {
        id: item.articleResponse.id,
        title: item.articleResponse.title,
        imageUrl: item.articleResponse.imageUrl ?? '',
        author: item.authorResponse,
      };
    }

    return item;
  }

  private normalizeArticle(article: ArticleWithUserResponse): ArticleWithUserResponse {
    return {
      articleResponse: {
        ...article.articleResponse,
        blocks: (article.articleResponse.blocks ?? []).map(block => this.normalizeBlock(block)),
      },
      authorResponse: article.authorResponse,
    };
  }

  private normalizeBlock(block: ArticleBlockResponse): ArticleBlockResponse {
    if (block.type !== 'IMAGE') {
      return block;
    }

    return {
      ...block,
      imageCaption: block.imageCaption ?? '',
    };
  }
}

export type SearchMode = 'title' | 'semantic';

interface LegacyArticleWithUserResponse {
  articleResponse: {
    id: string;
    title: string;
    imageUrl?: string;
    summary?: string;
    createdAt?: string;
    blocks?: ArticleBlockResponse[];
    published?: boolean;
  };
  authorResponse: {
    id: string;
    username: string;
    name: string;
  };
}
