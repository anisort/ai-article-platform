import { ArticleWithUserResponse } from './article-response.model';
import { ArticleImage } from './article-image.model';
import { ArticleAssistantChat, ArticleAssistantMessage } from '../../advisor/models/article-assistant.model';

export interface EditArticlePageResponse {
  article: ArticleWithUserResponse;
  images: ArticleImage[];
  chats: ArticleAssistantChat[];
  selectedChatId: string | null;
  messages: ArticleAssistantMessage[];
}
