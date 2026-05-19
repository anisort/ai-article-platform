import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import {
  ArticleAssistantChat,
  ArticleAssistantMessage,
  AssistantResponseMode,
  SendArticleAssistantMessageResponse
} from '../models/article-assistant.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ArticleAssistantService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlLlm}/v1/assistant/articles`;

  createChat(articleId: string, title: string): Observable<ArticleAssistantChat> {
    return this.http.post<ArticleAssistantChat>(`${this.apiUrl}/${articleId}/chats`, { title });
  }

  listMessages(articleId: string, chatId: string): Observable<ArticleAssistantMessage[]> {
    return this.http.get<ArticleAssistantMessage[]>(`${this.apiUrl}/${articleId}/chats/${chatId}/messages`);
  }

  sendMessage(
    articleId: string,
    chatId: string,
    message: string,
    mode: AssistantResponseMode
  ): Observable<SendArticleAssistantMessageResponse> {
    return this.http.post<SendArticleAssistantMessageResponse>(
      `${this.apiUrl}/${articleId}/chats/${chatId}/messages`,
      { message, mode }
    );
  }
}
