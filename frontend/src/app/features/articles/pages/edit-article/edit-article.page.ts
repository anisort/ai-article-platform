import { Component, ElementRef, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { IonIcon } from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import {
  chevronDownOutline,
  cloudUploadOutline,
  checkmarkOutline,
  codeSlashOutline,
  folderOpenOutline,
  imageOutline,
  linkOutline,
  listOutline,
  reorderThreeOutline,
  textOutline
} from 'ionicons/icons';
import { finalize } from 'rxjs';
import { marked } from 'marked';

import { EditorStateService } from '../../state/editor-state.service';
import { ArticlesService } from '../../services/articles.service';
import { ArticleImagesService } from '../../services/article-images.service';
import { ArticleWithUserResponse } from '../../models/article-response.model';
import { CreateArticleRequest } from '../../models/create-article-request.model';
import { UploadService } from '../../../../shared/services/upload.service';
import { ArticleImage } from '../../models/article-image.model';
import {
  ArticleAssistantChat,
  ArticleAssistantMessage,
  AssistantResponseMode
} from '../../../advisor/models/article-assistant.model';
import { ArticleAssistantService } from '../../../advisor/services/article-assistant.service';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  standalone: true,
  selector: 'app-edit-article-page',
  imports: [CommonModule, FormsModule, IonIcon, LoadingStateComponent],
  templateUrl: './edit-article.page.html',
  styleUrls: ['./edit-article.page.scss'],
})
export class EditArticlePage implements OnDestroy {
  @ViewChild('markdownInput') markdownInput?: ElementRef<HTMLTextAreaElement>;

  title = '';
  markdownContent = '';
  headingLevel: 1 | 2 | 3 | 4 = 2;
  headingDropdownOpen = false;
  isDraft = true;
  previewImageName = '';
  previewImageUrl = '';
  previewImageUploading = signal(false);
  articleImageUploading = signal(false);
  coverDialogOpen = signal(false);
  loadingArticle = signal(false);
  loadFailed = signal(false);
  articleImages = signal<ArticleImage[]>([]);
  imageLibraryOpen = signal(false);
  editorTab: 'write' | 'preview' = 'write';
  assistantChats = signal<ArticleAssistantChat[]>([]);
  selectedChatId = signal<string | null>(null);
  assistantMessages = signal<ArticleAssistantMessage[]>([]);
  assistantInput = '';
  assistantMode: AssistantResponseMode = 'THINKING';
  assistantLoading = signal(false);
  assistantOpen = signal(false);
  assistantWidthPx = signal(448);
  articleId: string | null = null;
  private isResizingAssistant = false;
  private onAssistantResizeMove?: (event: MouseEvent) => void;
  private onAssistantResizeUp?: () => void;

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private editorState = inject(EditorStateService);
  private articlesService = inject(ArticlesService);
  private articleImagesService = inject(ArticleImagesService);
  private uploadService = inject(UploadService);
  private articleAssistantService = inject(ArticleAssistantService);

  constructor() {
    addIcons({
      checkmarkOutline,
      chevronDownOutline,
      cloudUploadOutline,
      codeSlashOutline,
      folderOpenOutline,
      imageOutline,
      linkOutline,
      listOutline,
      reorderThreeOutline,
      textOutline
    });
    this.articleId = this.route.snapshot.paramMap.get('id');
    if (this.articleId) {
      this.loadArticleForEditing(this.articleId);
      this.loadArticleImages(this.articleId);
      this.loadAssistantChats(this.articleId);
    } else {
      this.restoreDraftState();
    }
  }

  ngOnDestroy() {
    this.stopAssistantResize();
  }

  private syncState() {
    this.editorState.setTitle(this.title);
    this.editorState.setMarkdownContent(this.markdownContent);
    this.editorState.setDraft(this.isDraft);
    this.editorState.setPreviewImage(this.previewImageUrl, this.previewImageName);
  }

  onTitleInput(event: Event) {
    this.title = (event.target as HTMLInputElement).value;
    this.syncState();
  }

  onContentInput(event: Event) {
    this.markdownContent = (event.target as HTMLTextAreaElement).value;
    this.syncState();
  }

  onDraftToggle(event: Event) {
    this.isDraft = (event.target as HTMLInputElement).checked;
    this.syncState();
  }

  renderMarkdown(text: string): string {
    return marked.parse(text, { breaks: true }) as string;
  }

  renderAssistantMarkdown(text: string): string {
    const html = marked.parse(text, { breaks: true }) as string;
    return html.replace(/(<pre><code[^>]*>)([\s\S]*?)(<\/code><\/pre>)/g, (full, openTag: string, codeHtml: string, closeTag: string) => {
      const langMatch = openTag.match(/language-([a-zA-Z0-9_-]+)/);
      const language = langMatch?.[1] ?? '';
      const decoded = this.decodeHtml(codeHtml);
      const payload = encodeURIComponent(decoded);
      return `<div class="assistant-code-block"><div class="assistant-code-block__actions"><button type="button" class="assistant-code-block__edit" data-code="${payload}" data-language="${language}">Edit code</button></div>${openTag}${codeHtml}${closeTag}</div>`;
    });
  }

  headingLabel(): string {
    return `h${this.headingLevel}`;
  }

  toggleHeadingDropdown() {
    this.headingDropdownOpen = !this.headingDropdownOpen;
  }

  selectHeading(level: 1 | 2 | 3 | 4) {
    this.headingLevel = level;
    this.headingDropdownOpen = false;
  }

  applyHeadingLevel() {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) {
      this.appendBlock(`${'#'.repeat(this.headingLevel)} Heading`);
      return;
    }

    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const lineStart = value.lastIndexOf('\n', Math.max(0, start - 1)) + 1;
    const lineEndRaw = value.indexOf('\n', end);
    const lineEnd = lineEndRaw === -1 ? value.length : lineEndRaw;
    const selected = value.slice(lineStart, lineEnd);
    const prefix = `${'#'.repeat(this.headingLevel)} `;
    const updatedLine = selected.replace(/^\s{0,3}#{1,6}\s+/, '').trim().length
      ? `${prefix}${selected.replace(/^\s{0,3}#{1,6}\s+/, '')}`
      : `${prefix}`;

    this.markdownContent = value.slice(0, lineStart) + updatedLine + value.slice(lineEnd);
    this.syncState();
    queueMicrotask(() => {
      const caret = lineStart + updatedLine.length;
      textarea.focus();
      textarea.setSelectionRange(caret, caret);
    });
  }

  appendWrap(prefix: string, suffix: string, placeholder: string) {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) {
      return;
    }
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const selected = value.slice(start, end);
    const content = selected || placeholder;
    const replacement = `${prefix}${content}${suffix}`;
    this.markdownContent = value.slice(0, start) + replacement + value.slice(end);
    this.syncState();
    queueMicrotask(() => {
      textarea.focus();
      if (selected) {
        textarea.setSelectionRange(start, start + replacement.length);
      } else {
        const caret = start + prefix.length + content.length;
        textarea.setSelectionRange(caret, caret);
      }
    });
  }

  appendBlock(text: string, placeholder = '') {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) {
      return;
    }
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const selected = value.slice(start, end);
    const replacement = selected ? text.replace(placeholder, selected) : text;
    this.markdownContent = value.slice(0, start) + replacement + value.slice(end);
    this.syncState();
    queueMicrotask(() => {
      textarea.focus();
      const caret = start + replacement.length;
      textarea.setSelectionRange(caret, caret);
    });
  }

  addBold() { this.appendWrap('**', '**', 'bold text'); }
  addItalic() { this.appendWrap('*', '*', 'italic text'); }
  addLink() { this.appendWrap('[', '](https://example.com)', 'link text'); }
  addList() { this.applyLinePrefix('- '); }
  addNumberedList() { this.applyNumberedList(); }
  addQuote() { this.applyLinePrefix('> '); }
  addCode() { this.appendBlock('```ts\n{{selection}}\n```', '{{selection}}'); }
  addImageMarkdown() { this.appendBlock('![caption]({{selection}})', '{{selection}}'); }

  private applyLinePrefix(prefix: string) {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) {
      return;
    }
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const blockStart = value.lastIndexOf('\n', Math.max(0, start - 1)) + 1;
    const blockEndRaw = value.indexOf('\n', end);
    const blockEnd = blockEndRaw === -1 ? value.length : blockEndRaw;
    const blockText = value.slice(blockStart, blockEnd);
    const transformed = blockText
      .split('\n')
      .map(line => `${prefix}${line}`)
      .join('\n');

    this.markdownContent = value.slice(0, blockStart) + transformed + value.slice(blockEnd);
    this.syncState();
    queueMicrotask(() => {
      textarea.focus();
      textarea.setSelectionRange(blockStart, blockStart + transformed.length);
    });
  }

  private applyNumberedList() {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) {
      return;
    }
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const blockStart = value.lastIndexOf('\n', Math.max(0, start - 1)) + 1;
    const blockEndRaw = value.indexOf('\n', end);
    const blockEnd = blockEndRaw === -1 ? value.length : blockEndRaw;
    const blockText = value.slice(blockStart, blockEnd);
    const transformed = blockText
      .split('\n')
      .map((line, idx) => `${idx + 1}. ${line}`)
      .join('\n');

    this.markdownContent = value.slice(0, blockStart) + transformed + value.slice(blockEnd);
    this.syncState();
    queueMicrotask(() => {
      textarea.focus();
      textarea.setSelectionRange(blockStart, blockStart + transformed.length);
    });
  }

  onPreviewSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.previewImageName = file.name;
    this.previewImageUploading.set(true);

    this.uploadService.uploadFile(file)
      .pipe(finalize(() => this.previewImageUploading.set(false)))
      .subscribe({
        next: response => {
          this.previewImageUrl = response.url;
          this.previewImageName = file.name;
          this.syncState();
          this.coverDialogOpen.set(false);
        },
        error: err => console.error('Preview upload failed', err),
      });
  }

  onArticleImageSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file || !this.articleId) return;

    this.articleImageUploading.set(true);
    this.uploadService.uploadFile(file)
      .pipe(finalize(() => this.articleImageUploading.set(false)))
      .subscribe({
        next: response => {
          this.articleImagesService.add(this.articleId!, response.url).subscribe({
            next: image => this.articleImages.update(images => [image, ...images]),
            error: err => console.error('Failed to save article image', err),
          });
        },
        error: err => console.error('Article image upload failed', err),
      });
  }

  openImageLibrary() {
    this.imageLibraryOpen.set(true);
  }

  closeImageLibrary() {
    this.imageLibraryOpen.set(false);
  }

  toggleCoverDialog() {
    this.coverDialogOpen.update(value => !value);
  }

  closeCoverDialog() {
    this.coverDialogOpen.set(false);
  }

  removeCoverImage() {
    this.previewImageUrl = '';
    this.previewImageName = '';
    this.syncState();
  }

  setEditorTab(tab: 'write' | 'preview') {
    this.editorTab = tab;
  }

  removeArticleImage(image: ArticleImage) {
    if (!this.articleId) return;
    this.articleImagesService.delete(this.articleId, image.id).subscribe({
      next: () => this.articleImages.update(images => images.filter(i => i.id !== image.id)),
      error: err => console.error('Failed to delete image', err),
    });
  }

  insertArticleImage(image: ArticleImage) {
    this.appendBlock(`![image](${image.imageUrl})`);
  }

  insertAssistantMessage(content: string) {
    const insert = () => {
      const textarea = this.markdownInput?.nativeElement;
      if (!textarea) {
        this.markdownContent = `${this.markdownContent}${this.markdownContent ? '\n\n' : ''}${content}`;
        this.syncState();
        return;
      }

      const value = this.markdownContent;
      const start = textarea.selectionStart ?? value.length;
      const end = textarea.selectionEnd ?? start;
      const replacement = `${content}\n`;
      this.markdownContent = value.slice(0, start) + replacement + value.slice(end);
      this.syncState();

      queueMicrotask(() => {
        const caret = start + replacement.length;
        textarea.focus();
        textarea.setSelectionRange(caret, caret);
      });
    };

    if (this.editorTab !== 'write') {
      this.editorTab = 'write';
      setTimeout(insert, 0);
      return;
    }

    insert();
  }

  onAssistantMessagesClick(event: MouseEvent) {
    const target = event.target as HTMLElement | null;
    const editButton = target?.closest('.assistant-code-block__edit') as HTMLElement | null;
    if (!editButton) {
      return;
    }

    event.preventDefault();
    event.stopPropagation();

    const payload = editButton.getAttribute('data-code');
    const language = editButton.getAttribute('data-language') ?? '';
    if (!payload) {
      return;
    }

    const code = decodeURIComponent(payload);
    const fenced = `\`\`\`${language}\n${code}\n\`\`\`\n`;
    this.insertAssistantMessage(fenced);
  }

  onAssistantResizeStart(event: MouseEvent) {
    if (window.innerWidth <= 1200) {
      return;
    }
    event.preventDefault();
    this.isResizingAssistant = true;
    document.body.style.cursor = 'col-resize';
    document.body.style.userSelect = 'none';

    this.onAssistantResizeMove = (moveEvent: MouseEvent) => {
      if (!this.isResizingAssistant) {
        return;
      }
      const minWidth = 320;
      const maxWidth = Math.max(minWidth, Math.floor(window.innerWidth * 0.55));
      const next = Math.round(window.innerWidth - moveEvent.clientX);
      const width = Math.min(maxWidth, Math.max(minWidth, next));
      this.assistantWidthPx.set(width);
    };

    this.onAssistantResizeUp = () => {
      this.stopAssistantResize();
    };

    document.addEventListener('mousemove', this.onAssistantResizeMove);
    document.addEventListener('mouseup', this.onAssistantResizeUp);
  }

  toggleAssistant() {
    this.assistantOpen.update(value => !value);
  }

  createAssistantChat() {
    if (!this.articleId) return;
    const title = `Chat ${this.assistantChats().length + 1}`;
    this.articleAssistantService.createChat(this.articleId, title).subscribe(chat => {
      this.assistantChats.update(chats => [chat, ...chats]);
      this.selectedChatId.set(chat.id);
      this.assistantMessages.set([]);
    });
  }

  selectAssistantChat(chatId: string) {
    this.selectedChatId.set(chatId);
    if (!this.articleId) return;
    this.articleAssistantService.listMessages(this.articleId, chatId).subscribe(messages => {
      this.assistantMessages.set(messages);
    });
  }

  sendAssistantMessage() {
    if (!this.articleId || !this.selectedChatId() || !this.assistantInput.trim() || this.assistantLoading()) {
      return;
    }

    const prompt = this.assistantInput.trim();
    this.assistantMessages.update(messages => [
      ...messages,
      { id: `tmp-${Date.now()}`, role: 'USER', content: prompt, createdAt: new Date().toISOString() }
    ]);
    this.assistantInput = '';
    this.assistantLoading.set(true);

    this.articleAssistantService.sendMessage(this.articleId, this.selectedChatId()!, prompt, this.assistantMode)
      .pipe(finalize(() => this.assistantLoading.set(false)))
      .subscribe({
        next: response => this.assistantMessages.update(messages => [...messages, response.assistantMessage]),
        error: () => this.assistantMessages.update(messages => [
          ...messages,
          { id: `err-${Date.now()}`, role: 'ASSISTANT', content: 'Assistant request failed. Try again.', createdAt: new Date().toISOString() }
        ])
      });
  }

  goToReview() {
    this.syncState();
    this.router.navigate(['/review-article']);
  }

  submitArticle() {
    const request: CreateArticleRequest = {
      title: this.title,
      imageUrl: this.previewImageUrl,
      published: !this.isDraft,
      markdownContent: this.markdownContent,
    };

    const request$ = this.articleId
      ? this.articlesService.update(this.articleId, request)
      : this.articlesService.create(request);

    request$.subscribe({
      next: res => {
        this.editorState.reset();
        this.router.navigate(['/articles', res.articleResponse.id]);
      },
      error: err => console.error(err),
    });
  }

  pageTitle(): string {
    return this.articleId ? 'Edit article' : 'New article';
  }

  submitLabel(): string {
    return this.articleId ? 'Save' : 'Upload';
  }

  private restoreDraftState() {
    this.title = this.editorState.title || '';
    this.markdownContent = this.editorState.markdownContent || '';
    this.isDraft = this.editorState.isDraft;
    this.previewImageUrl = this.editorState.previewImageUrl || '';
    this.previewImageName = this.editorState.previewImageName || '';
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

  private loadArticleImages(articleId: string) {
    this.articleImagesService.list(articleId).subscribe({
      next: images => this.articleImages.set(images),
      error: err => console.error('Failed to load article images', err),
    });
  }

  private loadAssistantChats(articleId: string) {
    this.articleAssistantService.listChats(articleId).subscribe(chats => {
      this.assistantChats.set(chats);
      if (!chats.length) {
        return;
      }
      this.selectedChatId.set(chats[0].id);
      this.selectAssistantChat(chats[0].id);
    });
  }

  private applyArticle(article: ArticleWithUserResponse) {
    const data = article.articleResponse;
    this.title = data.title;
    this.previewImageUrl = data.imageUrl ?? '';
    this.previewImageName = data.imageUrl ? 'Current preview image' : '';
    this.isDraft = !data.published;
    this.markdownContent = data.markdownContent ?? '';
  }

  private decodeHtml(value: string): string {
    const textarea = document.createElement('textarea');
    textarea.innerHTML = value;
    return textarea.value;
  }

  private stopAssistantResize() {
    this.isResizingAssistant = false;
    document.body.style.cursor = '';
    document.body.style.userSelect = '';
    if (this.onAssistantResizeMove) {
      document.removeEventListener('mousemove', this.onAssistantResizeMove);
      this.onAssistantResizeMove = undefined;
    }
    if (this.onAssistantResizeUp) {
      document.removeEventListener('mouseup', this.onAssistantResizeUp);
      this.onAssistantResizeUp = undefined;
    }
  }
}
