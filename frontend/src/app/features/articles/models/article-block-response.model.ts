export interface ArticleBlockResponse {
  id: string;
  position: number;
  type: 'TEXT' | 'IMAGE' | 'CODE';
  content?: string;
  codeLanguage?: string;
  code?: string;
  imageUrl?: string;
  imageCaption?: string;
}
