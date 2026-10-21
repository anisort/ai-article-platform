/**
 * Defines the assistant response mode type.
 */
export type AssistantResponseMode = 'THINKING' | 'CODE' | 'PRECISE';
/**
 * Defines the assistant message role type.
 */
export type AssistantMessageRole = 'USER' | 'ASSISTANT';

/**
 * Defines the assistant chat response interface.
 */
export interface AssistantChatResponse {
  id: string;
  title: string;
  createdAt: string;
}

/**
 * Defines the assistant message response interface.
 */
export interface AssistantMessageResponse {
  id: string;
  role: AssistantMessageRole;
  content: string;
  createdAt: string;
}

/**
 * Defines the assistant send message response interface.
 */
export interface AssistantSendMessageResponse {
  chatId: string;
  assistantMessage: AssistantMessageResponse;
}
