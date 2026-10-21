/**
 * Defines the advisor chat request interface.
 */
export interface AdvisorChatRequest {
  message: string;
  conversationId: string | null;
}

/**
 * Defines the advisor chat response interface.
 */
export interface AdvisorChatResponse {
  answer: string;
  conversationId: string;
}

/**
 * Defines the advisor message interface.
 */
export interface AdvisorMessage {
  role: 'user' | 'assistant';
  text: string;
}
