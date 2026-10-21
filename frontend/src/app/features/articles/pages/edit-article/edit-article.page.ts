import { Component, ElementRef, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ModalController } from '@ionic/angular/standalone';
import { finalize } from 'rxjs';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import { SegmentedControlComponent } from '../../../../shared/components/segmented-control/segmented-control.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { ArticleEditorToolbarComponent } from '../../components/article-editor-toolbar/article-editor-toolbar.component';
import { ArticleAssistantPanelComponent } from '../../components/article-assistant-panel/article-assistant-panel.component';
import { ArticleCoverModalComponent } from '../../components/article-cover-modal/article-cover-modal.component';
import { ArticleImagesModalComponent } from '../../components/article-images-modal/article-images-modal.component';
import { ArticlesService } from '../../services/articles.service';
import { ArticleImagesService } from '../../services/article-images.service';
import { ArticleWithUserResponse } from '../../models/article-response.model';
import { CreateArticleRequest } from '../../models/create-article-request.model';
import { UploadService } from '../../../../shared/services/upload.service';
import { ArticleImage } from '../../models/article-image.model';
import {
  AssistantChatResponse,
  AssistantMessageResponse,
  AssistantResponseMode
} from '../../../advisor/models/article-assistant.model';
import { ArticleAssistantService } from '../../../advisor/services/article-assistant.service';
import { PendingChangesComponent } from '../../../../core/guards/pending-changes.guard';
import { MarkdownRenderService } from '../../../../shared/services/markdown-render.service';

@Component({
  standalone: true,
  selector: 'app-edit-article-page',
  imports: [
    CommonModule,
    FormsModule,
    PageIntroComponent,
    SegmentedControlComponent,
    LoadingStateComponent,
    ArticleEditorToolbarComponent,
    ArticleAssistantPanelComponent
  ],
  templateUrl: './edit-article.page.html',
  styleUrls: ['./edit-article.page.scss'],
})
/**
 * Defines the edit article page class.
 */
export class EditArticlePage implements OnDestroy, PendingChangesComponent {
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
  loadingArticle = signal(false);
  loadFailed = signal(false);
  articleImages = signal<ArticleImage[]>([]);
  editorTab: 'write' | 'preview' = 'write';
  assistantChats = signal<AssistantChatResponse[]>([]);
  selectedChatId = signal<string | null>(null);
  assistantMessages = signal<AssistantMessageResponse[]>([]);
  assistantMode: AssistantResponseMode = 'THINKING';
  assistantOpen = signal(false);
  assistantPanelWidth = signal(448);
  draftCreationLoading = signal(false);
  savingArticle = signal(false);
  articleId: string | null = null;
  submitted = false;
  titleError = signal(false);
  contentError = signal(false);
  coverError = signal(false);
  private initialSnapshot = '';

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private articlesService = inject(ArticlesService);
  private articleImagesService = inject(ArticleImagesService);
  private uploadService = inject(UploadService);
  private articleAssistantService = inject(ArticleAssistantService);
  private modalController = inject(ModalController);
  private markdownRenderService = inject(MarkdownRenderService);
  private resizePointerId: number | null = null;
  private readonly pendingActionStorageKey = 'edit_article_pending_action';
  private readonly onResizePointerMove = (event: PointerEvent) => this.handleAssistantResize(event);
  private readonly onResizePointerUp = (event: PointerEvent) => this.stopAssistantResize(event);

  readonly editorTabs: { value: 'write' | 'preview'; label: string }[] = [
    { value: 'write', label: 'Write' },
    { value: 'preview', label: 'Preview' },
  ];

/**
 * Performs the constructor operation.
 */
  constructor() {
    this.articleId = this.route.snapshot.paramMap.get('id');
    if (this.articleId) {
      this.loadEditorData(this.articleId);
      return;
    }
    this.captureSnapshot();
  }

/**
 * Performs the ng on destroy operation.
 */
  ngOnDestroy() {
    this.teardownAssistantResize();
  }

/**
 * Performs the has pending changes operation.
 * @returns the operation result
 */
  hasPendingChanges(): boolean {
    return this.initialSnapshot !== this.snapshotState();
  }

/**
 * Performs the on title input operation.
 * @param event the event
 */
  onTitleInput(event: Event) {
    this.title = (event.target as HTMLInputElement).value;
    this.validateRequiredFields();
  }

/**
 * Performs the on content input operation.
 * @param event the event
 */
  onContentInput(event: Event) {
    this.markdownContent = (event.target as HTMLTextAreaElement).value;
    this.validateRequiredFields();
  }

/**
 * Performs the on draft toggle operation.
 * @param event the event
 */
  onDraftToggle(event: Event) {
    this.isDraft = (event.target as HTMLInputElement).checked;
  }

/**
 * Performs the render markdown operation.
 * @param text the text
 * @returns the operation result
 */
  renderMarkdown(text: string): string {
    return this.markdownRenderService.render(text);
  }

/**
 * Performs the render assistant markdown operation.
 * @param text the text
 * @returns the operation result
 */
  renderAssistantMarkdown(text: string): string {
    return this.markdownRenderService.renderAssistant(text);
  }

  toggleHeadingDropdown() { this.headingDropdownOpen = !this.headingDropdownOpen; }
  selectHeading(level: 1 | 2 | 3 | 4) { this.headingLevel = level; this.headingDropdownOpen = false; }
  setEditorTab(tab: 'write' | 'preview') { this.editorTab = tab; }

/**
 * Performs the apply heading level operation.
 */
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
    this.validateRequiredFields();
  }

/**
 * Performs the append wrap operation.
 * @param prefix the prefix
 * @param suffix the suffix
 * @param placeholder the placeholder
 */
  appendWrap(prefix: string, suffix: string, placeholder: string) {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) return;
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const selected = value.slice(start, end);
    const content = selected || placeholder;
    const replacement = `${prefix}${content}${suffix}`;
    this.markdownContent = value.slice(0, start) + replacement + value.slice(end);
    this.validateRequiredFields();
  }

/**
 * Performs the append block operation.
 * @param text the text
 * @param placeholder the placeholder
 */
  appendBlock(text: string, placeholder = '') {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) return;
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const selected = value.slice(start, end);
    const replacement = selected ? text.replace(placeholder, selected) : text;
    this.markdownContent = value.slice(0, start) + replacement + value.slice(end);
    this.validateRequiredFields();
  }

  addBold() { this.appendWrap('**', '**', 'bold text'); }
  addItalic() { this.appendWrap('*', '*', 'italic text'); }
  addStrike() { this.appendWrap('~~', '~~', 'strikethrough text'); }
  addLink() { this.appendWrap('[', '](https://example.com)', 'link text'); }
  addList() { this.applyLinePrefix('- '); }
  addNumberedList() { this.applyNumberedList(); }
  addQuote() { this.applyLinePrefix('> '); }
  addCode() { this.appendBlock('```ts\n{{selection}}\n```', '{{selection}}'); }
  addImageMarkdown() { this.appendBlock('![caption]({{selection}})', '{{selection}}'); }

/**
 * Performs the open cover dialog operation.
 */
  async openCoverDialog() {
    const modal = await this.modalController.create({
      component: ArticleCoverModalComponent,
      componentProps: { imageUrl: this.previewImageUrl, uploading: this.previewImageUploading() },
      cssClass: 'profile-connections-ionic-modal',
    });
    await modal.present();
    const { data } = await modal.onDidDismiss<{ action?: 'upload' | 'remove'; file?: File }>();
    if (data?.action === 'remove') {
      this.removeCoverImage();
    } else if (data?.action === 'upload' && data.file) {
      this.uploadPreviewFile(data.file);
    }
  }

/**
 * Performs the open image library operation.
 */
  async openImageLibrary() {
    if (!this.articleId) {
      await this.ensureDraftArticle('images');
      return;
    }
    const modal = await this.modalController.create({
      component: ArticleImagesModalComponent,
      componentProps: { articleId: this.articleId, uploading: this.articleImageUploading(), images: this.articleImages() },
      cssClass: 'article-images-ionic-modal',
    });
    await modal.present();
    const { data } = await modal.onDidDismiss<{ action?: 'upload' | 'insert' | 'remove'; file?: File; image?: ArticleImage }>();
    if (data?.action === 'upload' && data.file) {
      this.uploadArticleImage(data.file);
    } else if (data?.action === 'insert' && data.image) {
      this.insertArticleImage(data.image);
    } else if (data?.action === 'remove' && data.image) {
      this.removeArticleImage(data.image);
    }
  }

/**
 * Performs the remove cover image operation.
 */
  removeCoverImage() {
    this.previewImageUrl = '';
    this.previewImageName = '';
    this.validateRequiredFields();
  }

/**
 * Performs the insert article image operation.
 * @param image the image
 */
  insertArticleImage(image: ArticleImage) {
    this.appendBlock(`![image](${image.imageUrl})`);
  }

/**
 * Performs the remove article image operation.
 * @param image the image
 */
  removeArticleImage(image: ArticleImage) {
    if (!this.articleId) return;
    this.articleImagesService.delete(this.articleId, image.id).subscribe({
      next: () => this.articleImages.update(images => images.filter(i => i.id !== image.id)),
    });
  }

/**
 * Performs the toggle assistant operation.
 */
  async toggleAssistant() {
    if (this.draftCreationLoading()) {
      return;
    }
    if (!this.assistantOpen()) {
      if (!this.articleId) {
        await this.ensureDraftArticle('assistant');
        return;
      }
    }
    this.assistantOpen.update(value => !value);
  }

/**
 * Performs the start assistant resize operation.
 * @param event the event
 */
  startAssistantResize(event: PointerEvent): void {
    event.preventDefault();
    this.resizePointerId = event.pointerId;
    document.addEventListener('pointermove', this.onResizePointerMove);
    document.addEventListener('pointerup', this.onResizePointerUp);
  }

/**
 * Performs the on assistant messages click operation.
 * @param event the event
 */
  onAssistantMessagesClick(event: MouseEvent) {
    const target = event.target instanceof Element ? event.target : null;
    const copyButton = target?.closest('.article-code-copy') as HTMLElement | null;
    if (copyButton) {
      const pre = copyButton.closest('pre');
      const code = pre?.querySelector('code')?.textContent ?? '';
      this.copyText(code, copyButton);
    }
  }

/**
 * Performs the on preview click operation.
 * @param event the event
 */
  onPreviewClick(event: MouseEvent): void {
    const target = event.target instanceof Element ? event.target : null;
    const copyButton = target?.closest('.article-code-copy') as HTMLElement | null;
    if (!copyButton) return;
    const pre = copyButton.closest('pre');
    const code = pre?.querySelector('code')?.textContent ?? '';
    this.copyText(code, copyButton);
  }

/**
 * Performs the on assistant chat selected operation.
 * @param chatId the chatId
 */
  onAssistantChatSelected(chatId: string): void {
    const articleId = this.articleId;
    if (!articleId) return;
    this.selectedChatId.set(chatId);
    this.loadAssistantMessages(articleId, chatId);
  }

/**
 * Performs the insert assistant message operation.
 * @param content the content
 */
  insertAssistantMessage(content: string) {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) return;
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? value.length;
    const end = textarea.selectionEnd ?? start;
    const replacement = `${content}\n`;
    this.markdownContent = value.slice(0, start) + replacement + value.slice(end);
    this.editorTab = 'write';
    this.validateRequiredFields();
  }

/**
 * Performs the submit article operation.
 */
  submitArticle() {
    if (this.savingArticle()) {
      return;
    }

    this.submitted = true;
    this.validateRequiredFields();
    if (!this.canSubmit()) {
      return;
    }

    const request: CreateArticleRequest = {
      title: this.title.trim(),
      imageUrl: this.previewImageUrl,
      published: !this.isDraft,
      markdownContent: this.markdownContent.trim(),
    };

    const request$ = this.articleId ? this.articlesService.update(this.articleId, request) : this.articlesService.create(request);
    this.savingArticle.set(true);
    request$
      .pipe(finalize(() => this.savingArticle.set(false)))
      .subscribe({
      next: res => {
        this.captureSnapshot();
        this.router.navigate(['/articles', res.articleResponse.id]);
      },
    });
  }

  pageTitle(): string { return this.articleId ? 'Edit article' : 'Create article'; }
  submitLabel(): string { return this.articleId ? 'Save article' : 'Publish article'; }
  submittingLabel(): string { return this.articleId ? 'Saving...' : 'Publishing...'; }
  canSubmit(): boolean { return this.title.trim().length > 0 && this.markdownContent.trim().length > 0 && this.previewImageUrl.trim().length > 0; }
/**
 * Performs the validation missing items operation.
 * @returns the operation result
 */
  validationMissingItems(): string[] {
    const missing: string[] = [];
    if (!this.title.trim()) missing.push('Title');
    if (!this.markdownContent.trim()) missing.push('Content');
    if (!this.previewImageUrl.trim()) missing.push('Cover image');
    return missing;
  }

/**
 * Performs the upload preview file operation.
 * @param file the file
 */
  private uploadPreviewFile(file: File): void {
    this.previewImageName = file.name;
    this.previewImageUploading.set(true);
    this.uploadService.uploadFile(file)
      .pipe(finalize(() => this.previewImageUploading.set(false)))
      .subscribe({
        next: response => {
          this.previewImageUrl = response.url;
          this.validateRequiredFields();
        },
      });
  }

/**
 * Performs the upload article image operation.
 * @param file the file
 */
  private uploadArticleImage(file: File): void {
    if (!this.articleId) return;
    this.articleImageUploading.set(true);
    this.uploadService.uploadFile(file)
      .pipe(finalize(() => this.articleImageUploading.set(false)))
      .subscribe({
        next: response => {
          this.articleImagesService.add(this.articleId!, response.url).subscribe({
            next: image => this.articleImages.update(images => [image, ...images]),
          });
        },
      });
  }

/**
 * Performs the load editor data operation.
 * @param articleId the articleId
 */
  private loadEditorData(articleId: string) {
    this.loadingArticle.set(true);
    this.loadFailed.set(false);
    this.articlesService.getEditPage(articleId).subscribe({
      next: payload => {
        this.applyArticle(payload.article);
        this.articleImages.set(payload.images ?? []);
        this.assistantChats.set(payload.chats ?? []);
        this.selectedChatId.set(payload.selectedChatId ?? null);
        this.assistantMessages.set(payload.messages ?? []);
        this.captureSnapshot();
        this.loadingArticle.set(false);
        this.applyPendingAction();
      },
      error: (error: HttpErrorResponse) => {
        this.loadingArticle.set(false);
        if (error.status === 404 || error.status === 400) {
          this.router.navigate(['/not-found']);
          return;
        }
        this.loadFailed.set(true);
      }
    });
  }

/**
 * Performs the load assistant messages operation.
 * @param articleId the articleId
 * @param chatId the chatId
 */
  private loadAssistantMessages(articleId: string, chatId: string): void {
    this.articleAssistantService.listMessages(articleId, chatId).subscribe({
      next: messages => this.assistantMessages.set(messages),
      error: () => this.assistantMessages.set([]),
    });
  }

/**
 * Performs the apply article operation.
 * @param article the article
 */
  private applyArticle(article: ArticleWithUserResponse) {
    const data = article.articleResponse;
    this.title = data.title;
    this.previewImageUrl = data.imageUrl ?? '';
    this.previewImageName = data.imageUrl ? 'Current preview image' : '';
    this.isDraft = !data.published;
    this.markdownContent = data.markdownContent ?? '';
    this.validateRequiredFields();
  }

/**
 * Performs the validate required fields operation.
 */
  private validateRequiredFields(): void {
    this.titleError.set(this.submitted && !this.title.trim());
    this.contentError.set(this.submitted && !this.markdownContent.trim());
    this.coverError.set(this.submitted && !this.previewImageUrl.trim());
  }

/**
 * Performs the snapshot state operation.
 * @returns the operation result
 */
  private snapshotState(): string {
    return JSON.stringify({
      title: this.title,
      markdownContent: this.markdownContent,
      isDraft: this.isDraft,
      previewImageUrl: this.previewImageUrl,
    });
  }

/**
 * Performs the capture snapshot operation.
 */
  private captureSnapshot(): void {
    this.initialSnapshot = this.snapshotState();
  }

/**
 * Performs the apply line prefix operation.
 * @param prefix the prefix
 */
  private applyLinePrefix(prefix: string) {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) return;
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const blockStart = value.lastIndexOf('\n', Math.max(0, start - 1)) + 1;
    const blockEndRaw = value.indexOf('\n', end);
    const blockEnd = blockEndRaw === -1 ? value.length : blockEndRaw;
    const blockText = value.slice(blockStart, blockEnd);
    const transformed = blockText.split('\n').map(line => `${prefix}${line}`).join('\n');
    this.markdownContent = value.slice(0, blockStart) + transformed + value.slice(blockEnd);
    this.validateRequiredFields();
  }

/**
 * Performs the apply numbered list operation.
 */
  private applyNumberedList() {
    const textarea = this.markdownInput?.nativeElement;
    if (!textarea) return;
    const value = this.markdownContent;
    const start = textarea.selectionStart ?? 0;
    const end = textarea.selectionEnd ?? start;
    const blockStart = value.lastIndexOf('\n', Math.max(0, start - 1)) + 1;
    const blockEndRaw = value.indexOf('\n', end);
    const blockEnd = blockEndRaw === -1 ? value.length : blockEndRaw;
    const blockText = value.slice(blockStart, blockEnd);
    const transformed = blockText.split('\n').map((line, idx) => `${idx + 1}. ${line}`).join('\n');
    this.markdownContent = value.slice(0, blockStart) + transformed + value.slice(blockEnd);
    this.validateRequiredFields();
  }

/**
 * Performs the handle assistant resize operation.
 * @param event the event
 */
  private handleAssistantResize(event: PointerEvent): void {
    if (this.resizePointerId !== event.pointerId) return;
    const viewportWidth = window.innerWidth || 0;
    const nextWidth = viewportWidth - event.clientX;
    const minWidth = 352;
    const maxWidth = Math.max(minWidth, Math.floor(viewportWidth / 2));
    const bounded = Math.max(minWidth, Math.min(nextWidth, maxWidth));
    this.assistantPanelWidth.set(bounded);
  }

/**
 * Performs the stop assistant resize operation.
 * @param event the event
 */
  private stopAssistantResize(event: PointerEvent): void {
    if (this.resizePointerId !== event.pointerId) return;
    this.teardownAssistantResize();
  }

/**
 * Performs the teardown assistant resize operation.
 */
  private teardownAssistantResize(): void {
    this.resizePointerId = null;
    document.removeEventListener('pointermove', this.onResizePointerMove);
    document.removeEventListener('pointerup', this.onResizePointerUp);
  }

/**
 * Performs the copy text operation.
 * @param value the value
 * @param button the button
 * @returns the operation result
 */
  private async copyText(value: string, button: HTMLElement): Promise<void> {
    if (!value.trim()) {
      return;
    }
    const previous = button.textContent ?? 'Copy';
    try {
      if (navigator.clipboard?.writeText && window.isSecureContext) {
        await navigator.clipboard.writeText(value);
      } else {
        const textarea = document.createElement('textarea');
        textarea.value = value;
        textarea.style.position = 'fixed';
        textarea.style.opacity = '0';
        document.body.appendChild(textarea);
        textarea.focus();
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
      }
      button.textContent = 'Copied';
      setTimeout(() => {
        button.textContent = previous;
      }, 1200);
    } catch {
      button.textContent = 'Failed';
      setTimeout(() => {
        button.textContent = previous;
      }, 1200);
    }
  }

/**
 * Performs the ensure draft article operation.
 * @param action the action
 * @returns the operation result
 */
  private async ensureDraftArticle(action: 'assistant' | 'images'): Promise<void> {
    if (this.articleId || this.draftCreationLoading()) {
      return;
    }
    this.draftCreationLoading.set(true);

    const nextDraftIndex = Number(sessionStorage.getItem('create_article_draft_index') ?? '0') + 1;
    sessionStorage.setItem('create_article_draft_index', String(nextDraftIndex));

    const request: CreateArticleRequest = {
      title: this.title.trim() || `Draft ${nextDraftIndex}`,
      imageUrl: this.previewImageUrl || '',
      published: false,
      markdownContent: this.markdownContent.trim() || `Draft ${nextDraftIndex}`,
    };

    sessionStorage.setItem(this.pendingActionStorageKey, action);
    await new Promise<void>((resolve) => {
      this.articlesService.create(request).subscribe({
        next: (res) => {
          const createdId = res.articleResponse.id;
          this.articleId = createdId;
          this.applyArticle(res);
          this.captureSnapshot();
          this.router.navigate(['/edit-article', createdId], { replaceUrl: true });
          resolve();
        },
        error: () => {
          sessionStorage.removeItem(this.pendingActionStorageKey);
          this.draftCreationLoading.set(false);
          resolve();
        },
      });
    });
  }

/**
 * Performs the apply pending action operation.
 */
  private applyPendingAction(): void {
    const pendingAction = sessionStorage.getItem(this.pendingActionStorageKey);
    if (pendingAction !== 'assistant' && pendingAction !== 'images') {
      this.draftCreationLoading.set(false);
      return;
    }
    sessionStorage.removeItem(this.pendingActionStorageKey);
    this.draftCreationLoading.set(false);
    if (pendingAction === 'assistant') {
      this.assistantOpen.set(true);
      return;
    }
    this.openImageLibrary();
  }
}
