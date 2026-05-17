import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdvisorService } from '../../services/advisor.service';
import { AdvisorMessage } from '../../models/advisor-chat.model';
import { marked } from 'marked';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-advisor-widget',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingStateComponent],
  templateUrl: './advisor-widget.component.html',
  styleUrl: './advisor-widget.component.scss'
})
export class AdvisorWidgetComponent {
  private readonly advisorService = inject(AdvisorService);
  private readonly conversationKey = 'advisor_conversation_id';
  private readonly messagesKey = 'advisor_messages';

  isOpen = signal(false);
  isLoading = signal(false);
  input = '';
  messages = signal<AdvisorMessage[]>(this.loadMessages());
  conversationId = signal<string | null>(sessionStorage.getItem(this.conversationKey));

  toggle(): void {
    this.isOpen.update(value => !value);
  }

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

  clearSession(): void {
    sessionStorage.removeItem(this.conversationKey);
    sessionStorage.removeItem(this.messagesKey);
    this.conversationId.set(null);
    this.messages.set([]);
  }

  renderMarkdown(text: string): string {
    return marked.parse(text, { breaks: true }) as string;
  }

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

  private persistMessages(): void {
    sessionStorage.setItem(this.messagesKey, JSON.stringify(this.messages()));
  }
}
