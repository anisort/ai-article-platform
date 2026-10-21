import { ArticleWithUserResponse } from './article-response.model';
import { ArticleImage } from './article-image.model';
import { AssistantChatResponse, AssistantMessageResponse } from '../../advisor/models/article-assistant.model';

/**
 * Defines the edit article page response interface.
 */
export interface EditArticlePageResponse {
  article: ArticleWithUserResponse;
  images: ArticleImage[];
  chats: AssistantChatResponse[];
  selectedChatId: string | null;
  messages: AssistantMessageResponse[];
}
