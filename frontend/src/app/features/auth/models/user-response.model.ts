/**
 * Defines the user response interface.
 */
export interface UserResponse {
  id: string;
  username: string;
  name: string;
  email: string;
  avatar?: string | null;
}
