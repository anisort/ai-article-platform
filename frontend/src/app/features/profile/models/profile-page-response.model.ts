import { ArticlePreview } from '../../articles/models/article-preview.model';
import { UserProfile } from './user-profile.model';

/**
 * Defines the profile page response interface.
 */
export interface ProfilePageResponse {
  profile: UserProfile;
  ownArticles: ArticlePreview[];
  likedArticles: ArticlePreview[];
  publishedArticles: ArticlePreview[];
}
