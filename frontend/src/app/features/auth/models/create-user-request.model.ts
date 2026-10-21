/**
 * Defines the create user request interface.
 */
export interface CreateUserRequest {
  username: string;
  name: string;
  email: string;
  password: string;
  repeatPassword: string;
}
