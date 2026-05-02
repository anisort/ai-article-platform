import {Component, inject, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { EditorBlock, TextBlock, ImageBlock, CodeBlock } from '../../models/article-block.types';
import { TextBlockComponent } from '../../components/text-block/text-block.component';
import { ImageBlockComponent } from '../../components/image-block/image-block.component';
import { CodeBlockComponent } from '../../components/code-block/code-block.component';
import { EditorStateService } from '../../state/editor-state.service';
import {ArticlesService} from '../../services/articles.service';
import { ArticleBlockResponse, } from '../../models/article-block-response.model';
import { ArticleWithUserResponse } from '../../models/article-response.model';
import {CreateArticleRequest} from '../../models/create-article-request.model';
import {CreateArticleBlockRequest} from '../../models/create-article-block-request.model';
import {IonIcon} from '@ionic/angular/standalone';
import {addIcons} from 'ionicons';
import {cloudUploadOutline, codeSlashOutline, imageOutline, textOutline} from 'ionicons/icons';
import {UploadService} from '../../../../shared/services/upload.service';
import {finalize} from 'rxjs';

function uuid(): string {
  return Math.random().toString(16).slice(2) + '-' + Date.now().toString(16);
}

@Component({
  standalone: true,
  selector: 'app-edit-article-page',
  imports: [CommonModule, TextBlockComponent, ImageBlockComponent, CodeBlockComponent, IonIcon],
  templateUrl: './edit-article.page.html',
  styleUrls: ['./edit-article.page.scss'],
})
export class EditArticlePage {
  blocks: EditorBlock[] = [];
  title = '';
  isDraft = true;
  previewImageName = '';
  previewImageUrl = '';
  previewImageUploading = signal(false);
  loadingArticle = signal(false);
  loadFailed = signal(false);
  draggedBlockId: string | null = null;
  dragOverBlockId: string | null = null;
  articleId: string | null = null;

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private editorState = inject(EditorStateService);
  private articlesService = inject(ArticlesService);
  private uploadService = inject(UploadService);

  constructor() {
    addIcons({ imageOutline, textOutline, codeSlashOutline, cloudUploadOutline })

    this.articleId = this.route.snapshot.paramMap.get('id');

    if (this.articleId) {
      this.loadArticleForEditing(this.articleId);
    } else {
      this.restoreDraftState();
    }
  }

  trackById = (_: number, b: EditorBlock) => b.id;

  private syncState() {
    this.editorState.setTitle(this.title);
    this.editorState.setBlocks(this.blocks);
    this.editorState.setDraft(this.isDraft);
    this.editorState.setPreviewImage(this.previewImageUrl, this.previewImageName);
  }

  onTitleInput(event: Event) {
    const target = event.target as HTMLInputElement;
    this.title = target.value;
    this.syncState();
  }

  onDraftToggle(event: Event) {
    const target = event.target as HTMLInputElement;
    this.isDraft = target.checked;
    this.syncState();
  }

  onPreviewSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];

    if (!file) {
      return;
    }

    this.previewImageName = file.name;
    this.previewImageUploading.set(true);

    this.uploadService
      .uploadFile(file)
      .pipe(finalize(() => this.previewImageUploading.set(false)))
      .subscribe({
        next: response => {
          this.previewImageUrl = response.url;
          this.syncState();
        },
        error: err => {
          console.error('Preview upload failed', err);
        }
      });
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

  onBlockDragStart(id: string) {
    this.draggedBlockId = id;
    this.dragOverBlockId = null;
  }

  onBlockDragEnter(id: string) {
    if (!this.draggedBlockId || this.draggedBlockId === id) {
      this.dragOverBlockId = null;
      return;
    }

    this.dragOverBlockId = id;
  }

  onBlockDrop(targetId: string) {
    if (!this.draggedBlockId || this.draggedBlockId === targetId) {
      this.clearDragState();
      return;
    }

    const sourceIndex = this.blocks.findIndex(b => b.id === this.draggedBlockId);
    const targetIndex = this.blocks.findIndex(b => b.id === targetId);

    if (sourceIndex < 0 || targetIndex < 0) {
      this.clearDragState();
      return;
    }

    const nextBlocks = [...this.blocks];
    const [draggedBlock] = nextBlocks.splice(sourceIndex, 1);
    nextBlocks.splice(targetIndex, 0, draggedBlock);

    this.blocks = nextBlocks;
    this.renumber();
    this.syncState();
    this.clearDragState();
  }

  onBlockDragEnd() {
    this.clearDragState();
  }

  private clearDragState() {
    this.draggedBlockId = null;
    this.dragOverBlockId = null;
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

  submitArticle() {
    const request: CreateArticleRequest = {
      title: this.title,
      imageUrl: this.previewImageUrl,
      published: !this.isDraft,
      blocks: this.mapBlocks()
    };

    const request$ = this.articleId
      ? this.articlesService.update(this.articleId, request)
      : this.articlesService.create(request);

    request$.subscribe({
      next: res => {
        console.log(this.articleId ? 'Article updated' : 'Article created', res);
        this.editorState.reset();
        this.router.navigate(['/articles', res.articleResponse.id]);
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

  pageTitle(): string {
    return this.articleId ? 'Edit article' : 'New article';
  }

  submitLabel(): string {
    return this.articleId ? 'Save' : 'Upload';
  }

  private restoreDraftState() {
    if (this.editorState.title) {
      this.title = this.editorState.title;
    }
    if (this.editorState.blocks.length) {
      this.blocks = this.editorState.blocks.map(b => ({ ...b })) as EditorBlock[];
    }
    this.isDraft = this.editorState.isDraft;
    if (this.editorState.previewImageUrl) {
      this.previewImageUrl = this.editorState.previewImageUrl;
      this.previewImageName = this.editorState.previewImageName;
    }
  }

  private loadArticleForEditing(articleId: string) {
    this.loadingArticle.set(true);
    this.loadFailed.set(false);

    this.articlesService.getById(articleId).subscribe({
      next: article => {
        this.applyArticle(article);
        this.syncState();
        this.loadingArticle.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Failed to load article for editing', error);
        this.loadingArticle.set(false);
        this.loadFailed.set(true);
      }
    });
  }

  private applyArticle(article: ArticleWithUserResponse) {
    const data = article.articleResponse;

    this.title = data.title;
    this.previewImageUrl = data.imageUrl ?? '';
    this.previewImageName = data.imageUrl ? 'Current preview image' : '';
    this.isDraft = !data.published;
    this.blocks = data.blocks
      .slice()
      .sort((left, right) => left.position - right.position)
      .map(block => this.mapBlockToEditor(block));
  }

  private mapBlockToEditor(block: ArticleBlockResponse): EditorBlock {
    switch (block.type) {
      case 'TEXT':
        return {
          id: block.id,
          type: 'TEXT',
          position: block.position,
          content: block.content ?? '',
        };

      case 'IMAGE':
        return {
          id: block.id,
          type: 'IMAGE',
          position: block.position,
          imageUrl: block.imageUrl,
          caption: block.imageCaption ?? '',
        };

      case 'CODE':
        return {
          id: block.id,
          type: 'CODE',
          position: block.position,
          codeLanguage: block.codeLanguage ?? '',
          code: block.code ?? '',
        };
    }
  }
}
