import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import {
  AssistantChatResponse,
  AssistantMessageResponse,
  AssistantSendMessageResponse,
  AssistantResponseMode,
} from '../models/article-assistant.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
/**
 * Defines the article assistant service class.
 */
export class ArticleAssistantService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlLlm}/v1/assistant/articles`;

/**
 * Performs the create chat operation.
 * @param articleId the articleId
 * @param title the title
 * @returns the operation result
 */
  createChat(articleId: string, title: string): Observable<AssistantChatResponse> {
    return this.http.post<AssistantChatResponse>(`${this.apiUrl}/${articleId}/chats`, { title });
  }

/**
 * Performs the list messages operation.
 * @param articleId the articleId
 * @param chatId the chatId
 * @returns the operation result
 */
  listMessages(articleId: string, chatId: string): Observable<AssistantMessageResponse[]> {
    return this.http.get<AssistantMessageResponse[]>(`${this.apiUrl}/${articleId}/chats/${chatId}/messages`);
  }

  sendMessage(
    articleId: string,
    chatId: string,
    message: string,
    mode: AssistantResponseMode
  ): Observable<AssistantSendMessageResponse> {
    return this.http.post<AssistantSendMessageResponse>(
      `${this.apiUrl}/${articleId}/chats/${chatId}/messages`,
      { message, mode }
    );
  }
}
