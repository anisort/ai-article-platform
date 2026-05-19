export type AssistantResponseMode = 'THINKING' | 'CODE' | 'PRECISE';
export type AssistantMessageRole = 'USER' | 'ASSISTANT';

export interface AssistantChatResponse {
  id: string;
  title: string;
  createdAt: string;
}

export interface AssistantMessageResponse {
  id: string;
  role: AssistantMessageRole;
  content: string;
  createdAt: string;
}

export interface AssistantSendMessageResponse {
  chatId: string;
  assistantMessage: AssistantMessageResponse;
}
