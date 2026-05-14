export interface AuthorPreview {
  id: string;
  username: string;
  name: string;
}

export interface ArticlePreview {
  id: string;
  title: string;
  imageUrl: string;
  createdAt?: string;
  published: boolean;
  author: AuthorPreview;
}
