/**
 * Defines the author preview interface.
 */
export interface AuthorPreview {
  id: string;
  username: string;
  name: string;
  avatar?: string | null;
}

/**
 * Defines the article preview interface.
 */
export interface ArticlePreview {
  id: string;
  title: string;
  imageUrl: string;
  createdAt?: string;
  published: boolean;
  author: AuthorPreview;
}
