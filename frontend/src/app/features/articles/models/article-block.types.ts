export type BlockType = 'TEXT' | 'IMAGE' | 'CODE';

export interface BaseBlock {
  id: string;
  position: number;
  type: BlockType;
}

export interface TextBlock extends BaseBlock {
  type: 'TEXT';
  content: string;
}

export interface ImageBlock extends BaseBlock {
  type: 'IMAGE';
  imageUrl?: string;
  caption?: string;
  file?: File;
}

export interface CodeBlock extends BaseBlock {
  type: 'CODE';
  codeLanguage?: string;
  code: string;
}

export type EditorBlock = TextBlock | ImageBlock | CodeBlock;
