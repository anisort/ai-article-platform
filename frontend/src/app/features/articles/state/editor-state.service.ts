// editor-state.service.ts
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class EditorStateService {
  title = '';
  markdownContent = '';
  isDraft = true;
  previewImageUrl = '';
  previewImageName = '';

  constructor() {
    console.log('EditorStateService created', Math.random());
  }


  setTitle(t: string) {
    this.title = t;
  }

  setMarkdownContent(markdownContent: string) {
    this.markdownContent = markdownContent;
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
    this.markdownContent = '';
    this.isDraft = true;
    this.previewImageUrl = '';
    this.previewImageName = '';
  }
}
