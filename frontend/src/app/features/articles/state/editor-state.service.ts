// editor-state.service.ts
import { Injectable } from '@angular/core';
import { EditorBlock } from '../models/article-block.types';

@Injectable({ providedIn: 'root' })
export class EditorStateService {
  title = '';
  blocks: EditorBlock[] = [];

  constructor() {
    console.log('EditorStateService created', Math.random());
  }


  setTitle(t: string) {
    this.title = t;
  }

  setBlocks(blocks: EditorBlock[]) {
    this.blocks = blocks;
  }

  reset() {
    this.title = '';
    this.blocks = [];
  }
}
