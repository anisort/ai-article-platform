/**
 * Defines the user summary interface.
 */
export interface UserSummary {
  id: string;
  username: string;
  name: string;
  email?: string | null;
  avatar?: string | null;
}

/**
 * Defines the user profile interface.
 */
export interface UserProfile {
  id: string;
  username: string;
  name: string;
  email?: string | null;
  avatar?: string | null;
  ownProfile: boolean;
  following: boolean;
  followers: UserSummary[];
  followingUsers: UserSummary[];
}

/**
 * Defines the toggle follow response interface.
 */
export interface ToggleFollowResponse {
  targetUserId: string;
  following: boolean;
}
