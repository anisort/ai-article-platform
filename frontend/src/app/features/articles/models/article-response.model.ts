import {ArticleBlockResponse} from './article-block-response.model';

export interface ArticleResponse {
  id: string;
  title: string;
  summary: string;
  createdAt: string;
  blocks: ArticleBlockResponse[];
  published: boolean;
}

export interface AuthorResponse {
  id: string;
  username: string;
  name: string;
}

export interface ArticleWithUserResponse {
  article: ArticleResponse;
  author: AuthorResponse;
}
