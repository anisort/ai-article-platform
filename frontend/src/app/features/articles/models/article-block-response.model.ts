export interface ArticleBlockResponse {
  id: string;
  position: number;
  type: string;
  content?: string;
  codeLanguage?: string;
  code?: string;
  imageUrl?: string;
  caption?: string;
}
