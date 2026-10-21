/**
 * Defines the create article request interface.
 */
export interface CreateArticleRequest {
  title: string;
  imageUrl: string;
  published: boolean;
  markdownContent: string;
}
