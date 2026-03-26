import {CreateArticleBlockRequest} from './create-article-block-request.model';

export interface CreateArticleRequest {
  title: string;
  imageUrl: string;
  published: boolean;
  blocks: CreateArticleBlockRequest[];
}
