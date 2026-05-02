export interface AuthorPreview {
  id: string;
  username: string;
  name: string;
}

export interface PublishedArticlePreview {
  id: string;
  title: string;
  imageUrl: string;
  author: AuthorPreview;
}
