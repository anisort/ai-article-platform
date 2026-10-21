import { CommonModule } from '@angular/common';
import { Component, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { finalize } from 'rxjs';
import {
  AssistantChatResponse,
  AssistantMessageResponse,
  AssistantResponseMode
} from '../../../advisor/models/article-assistant.model';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { SelectFieldComponent, SelectFieldOption } from '../../../../shared/components/select-field/select-field.component';
import { MessageBubbleComponent } from '../../../../shared/components/message-bubble/message-bubble.component';
import { ArticleAssistantService } from '../../../advisor/services/article-assistant.service';

@Component({
  selector: 'app-article-assistant-panel',
  standalone: true,
  imports: [CommonModule, LoadingStateComponent, SelectFieldComponent, MessageBubbleComponent],
  templateUrl: './article-assistant-panel.component.html',
  styleUrl: './article-assistant-panel.component.scss',
})
/**
 * Defines the article assistant panel component class.
 */
export class ArticleAssistantPanelComponent implements OnChanges {
  readonly articleId = input<string | null>(null);
  readonly initialChats = input<AssistantChatResponse[]>([]);
  readonly initialSelectedChatId = input<string | null>(null);
  readonly initialMessages = input<AssistantMessageResponse[]>([]);
  readonly initialMode = input<AssistantResponseMode>('THINKING');
  readonly rendered = input.required<(value: string) => string>();

  readonly closePanel = output<void>();
  readonly insertMessage = output<string>();
  readonly messagesClick = output<MouseEvent>();
  readonly chatSelected = output<string>();
  readonly loading = signal(false);
  readonly chats = signal<AssistantChatResponse[]>([]);
  readonly selectedChatId = signal<string | null>(null);
  readonly messages = signal<AssistantMessageResponse[]>([]);
  readonly mode = signal<AssistantResponseMode>('THINKING');
  readonly inputText = signal('');

  private readonly articleAssistantService = inject(ArticleAssistantService);

  readonly modeOptions: SelectFieldOption[] = [
    { value: 'THINKING', label: 'Thinking' },
    { value: 'CODE', label: 'Code' },
    { value: 'PRECISE', label: 'Precise' },
  ];

/**
 * Performs the ng on changes operation.
 * @param changes the changes
 */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['initialChats']) {
      this.chats.set([...(this.initialChats() ?? [])]);
    }
    if (changes['initialMode']) {
      this.mode.set(this.initialMode());
    }
    if (changes['initialSelectedChatId']) {
      this.selectedChatId.set(this.initialSelectedChatId());
    }
    if (changes['initialMessages']) {
      this.messages.set([...(this.initialMessages() ?? [])]);
    }
  }

/**
 * Performs the create chat operation.
 */
  createChat(): void {
    const articleId = this.articleId();
    if (!articleId) return;
    const title = `Chat ${this.chats().length + 1}`;
    this.loading.set(true);
    this.articleAssistantService.createChat(articleId, title)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe(chat => {
        this.chats.update(chats => [chat, ...chats]);
        this.selectedChatId.set(chat.id);
        this.messages.set([]);
      });
  }

/**
 * Performs the chat options operation.
 * @returns the operation result
 */
  chatOptions(): SelectFieldOption[] {
    const items = this.chats().map(chat => ({ value: chat.id, label: chat.title }));
    if (!items.length) {
      return [{ value: '', label: 'No chats yet', disabled: true }];
    }
    return items;
  }

/**
 * Performs the on input change operation.
 * @param event the event
 */
  onInputChange(event: Event): void {
    const target = event.target as HTMLTextAreaElement | null;
    this.inputText.set(target?.value ?? '');
  }

/**
 * Performs the on input keydown operation.
 * @param event the event
 */
  onInputKeydown(event: KeyboardEvent): void {
    if (event.key !== 'Enter' || event.shiftKey) {
      return;
    }
    event.preventDefault();
    if (!this.loading()) {
      this.sendMessage();
    }
  }

/**
 * Performs the on chat select operation.
 * @param event the event
 */
  onChatSelect(event: Event | string): void {
    const target = typeof event === 'string'
      ? { value: event }
      : (event.target as HTMLSelectElement | null);
    const chatId = target?.value?.trim();
    if (chatId) {
      this.selectedChatId.set(chatId);
      this.chatSelected.emit(chatId);
    }
  }

/**
 * Performs the on mode select operation.
 * @param event the event
 */
  onModeSelect(event: Event | string): void {
    const target = typeof event === 'string'
      ? { value: event }
      : (event.target as HTMLSelectElement | null);
    const nextMode = target?.value as AssistantResponseMode | undefined;
    if (nextMode === 'THINKING' || nextMode === 'CODE' || nextMode === 'PRECISE') {
      this.mode.set(nextMode);
    }
  }

/**
 * Performs the send message operation.
 */
  sendMessage(): void {
    const articleId = this.articleId();
    const selectedChatId = this.selectedChatId();
    const prompt = this.inputText().trim();
    if (!articleId || !selectedChatId || !prompt || this.loading()) return;

    this.messages.update(items => [...items, { id: `tmp-${Date.now()}`, role: 'USER', content: prompt, createdAt: new Date().toISOString() }]);
    this.inputText.set('');
    this.loading.set(true);
    this.articleAssistantService.sendMessage(articleId, selectedChatId, prompt, this.mode())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: response => this.messages.update(items => [...items, response.assistantMessage]),
        error: () => this.messages.update(items => [...items, { id: `err-${Date.now()}`, role: 'ASSISTANT', content: 'Assistant request failed. Try again.', createdAt: new Date().toISOString() }])
      });
  }

}
