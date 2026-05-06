export interface ArticleResponse {
  id: string;
  title: string;
  imageUrl?: string;
  summary: string;
  markdownContent?: string;
  createdAt: string;
  published: boolean;
}

export interface AuthorResponse {
  id: string;
  username: string;
  name: string;
}

export interface ArticleWithUserResponse {
  articleResponse: ArticleResponse;
  authorResponse: AuthorResponse;
}
