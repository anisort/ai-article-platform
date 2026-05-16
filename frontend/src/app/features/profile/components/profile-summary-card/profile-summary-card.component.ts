import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

import { UserProfile } from '../../models/user-profile.model';
import { AvatarComponent } from '../../../../shared/components/avatar/avatar.component';

type ConnectionTab = 'following' | 'followers' | null;

@Component({
  selector: 'app-profile-summary-card',
  standalone: true,
  imports: [CommonModule, AvatarComponent],
  templateUrl: './profile-summary-card.component.html',
  styleUrl: './profile-summary-card.component.scss',
})
export class ProfileSummaryCardComponent {
  readonly profile = input.required<UserProfile>();
  readonly connectionTab = input<ConnectionTab>(null);
  readonly followLoading = input(false);
  readonly canFollow = input(false);
  readonly showStats = input(true);

  readonly connectionSelect = output<'following' | 'followers'>();
  readonly followToggle = output<void>();
  readonly backToArticles = output<void>();

  displayName(): string {
    const profile = this.profile();
    return profile.name || profile.username || 'Unknown author';
  }

  profileHandle(): string | null {
    const username = this.profile().username?.trim();
    return username ? `@${username}` : null;
  }

  followersCount(): number {
    return this.profile().followers.length;
  }

  followingCount(): number {
    return this.profile().followingUsers.length;
  }
}
