// editor-state.service.ts
import { Injectable } from '@angular/core';
import { EditorBlock } from '../models/article-block.types';

@Injectable({ providedIn: 'root' })
export class EditorStateService {
  title = '';
  blocks: EditorBlock[] = [];
  isDraft = true;
  previewImageUrl = '';
  previewImageName = '';

  constructor() {
    console.log('EditorStateService created', Math.random());
  }


  setTitle(t: string) {
    this.title = t;
  }

  setBlocks(blocks: EditorBlock[]) {
    this.blocks = blocks;
  }

  setDraft(isDraft: boolean) {
    this.isDraft = isDraft;
  }

  setPreviewImage(previewImageUrl: string, previewImageName: string) {
    this.previewImageUrl = previewImageUrl;
    this.previewImageName = previewImageName;
  }

  reset() {
    this.title = '';
    this.blocks = [];
    this.isDraft = true;
    this.previewImageUrl = '';
    this.previewImageName = '';
  }
}
