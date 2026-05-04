export interface AdvisorChatRequest {
  message: string;
  conversationId: string | null;
}

export interface AdvisorChatResponse {
  answer: string;
  conversationId: string;
}

export interface AdvisorMessage {
  role: 'user' | 'assistant';
  text: string;
}
