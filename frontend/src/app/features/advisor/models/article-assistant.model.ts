export type AssistantResponseMode = 'THINKING' | 'CODE' | 'PRECISE';
export type AssistantMessageRole = 'USER' | 'ASSISTANT';

export interface ArticleAssistantChat {
  id: string;
  title: string;
  createdAt: string;
}

export interface ArticleAssistantMessage {
  id: string;
  role: AssistantMessageRole;
  content: string;
  createdAt: string;
}

export interface SendArticleAssistantMessageResponse {
  chatId: string;
  assistantMessage: ArticleAssistantMessage;
}
