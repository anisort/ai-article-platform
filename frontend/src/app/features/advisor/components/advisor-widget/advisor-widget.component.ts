import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdvisorService } from '../../services/advisor.service';
import { AdvisorMessage } from '../../models/advisor-chat.model';
import { marked } from 'marked';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { MessageBubbleComponent } from '../../../../shared/components/message-bubble/message-bubble.component';

@Component({
  selector: 'app-advisor-widget',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingStateComponent, MessageBubbleComponent],
  templateUrl: './advisor-widget.component.html',
  styleUrl: './advisor-widget.component.scss'
})
/**
 * Defines the advisor widget component class.
 */
export class AdvisorWidgetComponent {
  private readonly advisorService = inject(AdvisorService);
  private readonly conversationKey = 'advisor_conversation_id';
  private readonly messagesKey = 'advisor_messages';

  isOpen = signal(false);
  isLoading = signal(false);
  input = '';
  messages = signal<AdvisorMessage[]>(this.loadMessages());
  conversationId = signal<string | null>(sessionStorage.getItem(this.conversationKey));

/**
 * Performs the toggle operation.
 */
  toggle(): void {
    this.isOpen.update(value => !value);
  }

/**
 * Performs the send operation.
 */
  send(): void {
    const message = this.input.trim();
    if (!message || this.isLoading()) {
      return;
    }

    this.messages.update(items => [...items, { role: 'user', text: message }]);
    this.persistMessages();
    this.input = '';
    this.isLoading.set(true);

    this.advisorService.chat({
      message,
      conversationId: this.conversationId()
    }).subscribe({
      next: response => {
        this.conversationId.set(response.conversationId);
        sessionStorage.setItem(this.conversationKey, response.conversationId);
        this.messages.update(items => [...items, { role: 'assistant', text: response.answer || 'No response.' }]);
        this.persistMessages();
      },
      error: () => {
        this.messages.update(items => [...items, { role: 'assistant', text: 'Request failed. Try again.' }]);
        this.persistMessages();
      },
      complete: () => {
        this.isLoading.set(false);
      }
    });
  }

/**
 * Performs the clear session operation.
 */
  clearSession(): void {
    sessionStorage.removeItem(this.conversationKey);
    sessionStorage.removeItem(this.messagesKey);
    this.conversationId.set(null);
    this.messages.set([]);
  }

/**
 * Performs the render markdown operation.
 * @param text the text
 * @returns the operation result
 */
  renderMarkdown(text: string): string {
    return marked.parse(text, { breaks: true }) as string;
  }

/**
 * Performs the load messages operation.
 * @returns the operation result
 */
  private loadMessages(): AdvisorMessage[] {
    const raw = sessionStorage.getItem(this.messagesKey);
    if (!raw) {
      return [];
    }

    try {
      const parsed = JSON.parse(raw) as AdvisorMessage[];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }

/**
 * Performs the persist messages operation.
 */
  private persistMessages(): void {
    sessionStorage.setItem(this.messagesKey, JSON.stringify(this.messages()));
  }
}
