export interface UserSummary {
  id: string;
  username: string;
  name: string;
  email?: string | null;
  avatar?: string | null;
}

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

export interface ToggleFollowResponse {
  targetUserId: string;
  following: boolean;
}
