import {Component, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { EditorBlock, TextBlock, ImageBlock, CodeBlock } from '../../models/article-block.types';
import { TextBlockComponent } from '../../components/text-block/text-block.component';
import { ImageBlockComponent } from '../../components/image-block/image-block.component';
import { CodeBlockComponent } from '../../components/code-block/code-block.component';
import { EditorStateService } from '../../state/editor-state.service';
import {ArticlesService} from '../../services/articles.service';
import {CreateArticleRequest} from '../../models/create-article-request.model';
import {CreateArticleBlockRequest} from '../../models/create-article-block-request.model';

function uuid(): string {
  return Math.random().toString(16).slice(2) + '-' + Date.now().toString(16);
}

@Component({
  standalone: true,
  selector: 'app-edit-article-page',
  imports: [CommonModule, TextBlockComponent, ImageBlockComponent, CodeBlockComponent],
  templateUrl: './edit-article.page.html',
  styleUrls: ['./edit-article.page.scss'],
})
export class EditArticlePage {
  blocks: EditorBlock[] = [];
  title = 'Edit Article';

  private router = inject(Router);
  private editorState = inject(EditorStateService);
  private articlesService = inject(ArticlesService);

  constructor() {
    if (this.editorState.title) {
      this.title = this.editorState.title;
    }
    if (this.editorState.blocks.length) {
      this.blocks = this.editorState.blocks.map(b => ({ ...b })) as EditorBlock[];
    }
  }

  trackById = (_: number, b: EditorBlock) => b.id;

  private syncState() {
    this.editorState.setTitle(this.title);
    this.editorState.setBlocks(this.blocks);
  }

  addTextBlock() {
    const b: TextBlock = { id: uuid(), type: 'TEXT', position: this.blocks.length + 1, content: '' };
    this.blocks = [...this.blocks, b];
    this.syncState();
  }

  addImageBlock() {
    const b: ImageBlock = { id: uuid(), type: 'IMAGE', position: this.blocks.length + 1, caption: '' };
    this.blocks = [...this.blocks, b];
    this.syncState();
  }

  addCodeBlock() {
    const b: CodeBlock = { id: uuid(), type: 'CODE', position: this.blocks.length + 1, codeLanguage: '', code: '' };
    this.blocks = [...this.blocks, b];
    this.syncState();
  }

  updateBlock(updated: EditorBlock) {
    this.blocks = this.blocks.map(b => (b.id === updated.id ? updated : b));
    this.renumber();
    this.syncState();
  }

  removeBlock(id: string) {
    this.blocks = this.blocks.filter(b => b.id !== id);
    this.renumber();
    this.syncState();
  }

  moveUp(id: string) {
    const i = this.blocks.findIndex(b => b.id === id);
    if (i <= 0) return;

    const copy = [...this.blocks];
    [copy[i - 1], copy[i]] = [copy[i], copy[i - 1]];
    this.blocks = copy;

    this.renumber();
    this.syncState();
  }

  moveDown(id: string) {
    const i = this.blocks.findIndex(b => b.id === id);
    if (i < 0 || i >= this.blocks.length - 1) return;

    const copy = [...this.blocks];
    [copy[i], copy[i + 1]] = [copy[i + 1], copy[i]];
    this.blocks = copy;

    this.renumber();
    this.syncState();
  }

  private renumber() {
    this.blocks = this.blocks.map((b, idx) => ({ ...b, position: idx + 1 }) as EditorBlock);
  }

  goToReview() {
    this.syncState();
    this.router.navigate(['/review-article']);
  }

    logPayload() {
    console.log(
      this.blocks.map(b => {
        if (b.type === 'TEXT') return { position: b.position, type: 'PARAGRAPH', content: b.content };
        if (b.type === 'IMAGE') return { position: b.position, type: 'IMAGE', imageUrl: b.imageUrl, imageCaption: b.caption };
        return { position: b.position, type: 'CODE', codeLanguage: b.codeLanguage, code: b.code };
      })
    );
  }

  createArticle() {
    const request: CreateArticleRequest = {
      title: this.title,
      imageUrl: '',
      published: false,
      blocks: this.mapBlocks()
    };

    this.articlesService.create(request).subscribe({
      next: res => {
        console.log('Article created', res);
        this.router.navigate(['/articles', res.article.id]);
      },
      error: err => {
        console.error(err);
      }
    });

  }

  private mapBlocks(): CreateArticleBlockRequest[] {
    return this.blocks.map(b => {
      switch (b.type) {

        case 'TEXT':
          return {
            position: b.position,
            type: 'TEXT',
            content: b.content
          };

        case 'IMAGE':
          return {
            position: b.position,
            type: 'IMAGE',
            imageUrl: b.imageUrl,
            imageCaption: b.caption
          };

        case 'CODE':
          return {
            position: b.position,
            type: 'CODE',
            codeLanguage: b.codeLanguage,
            code: b.code
          };
      }
    });
  }
}
