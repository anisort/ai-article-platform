export interface CreateArticleBlockRequest {
  position: number;
  type: 'TEXT' | 'IMAGE' | 'CODE';
  content?: string;
  codeLanguage?: string;
  code?: string;
  imageUrl?: string;
  imageCaption?: string;
}
