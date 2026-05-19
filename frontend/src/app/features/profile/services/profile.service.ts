import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { ProfilePageResponse } from '../models/profile-page-response.model';
import { ToggleFollowResponse, UserProfile } from '../models/user-profile.model';

export interface ToggleArticleLikeResponse {
  articleId: string;
  liked: boolean;
  likesCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlAuth}/v1/users`;
  private readonly profilePageApiUrl = `${environment.apiUrlArticles}/v1/profile-page`;

  getProfilePage(userId?: string): Observable<ProfilePageResponse> {
    return this.http.get<ProfilePageResponse>(
      userId ? `${this.profilePageApiUrl}/${userId}` : `${this.profilePageApiUrl}/me`
    );
  }

  toggleFollow(userId: string): Observable<ToggleFollowResponse> {
    return this.http.post<ToggleFollowResponse>(`${this.apiUrl}/${userId}/follow`, {});
  }

  toggleArticleLike(articleId: string): Observable<ToggleArticleLikeResponse> {
    return this.http.post<ToggleArticleLikeResponse>(`${this.profilePageApiUrl}/articles/${articleId}/like`, {});
  }

  updateOwnProfile(username: string, name: string, avatar?: File | null): Observable<UserProfile> {
    const formData = new FormData();
    formData.append('username', username);
    formData.append('name', name);
    if (avatar) {
      formData.append('avatar', avatar);
    }

    return this.http.put<UserProfile>(`${this.apiUrl}/me/profile`, formData);
  }
}
