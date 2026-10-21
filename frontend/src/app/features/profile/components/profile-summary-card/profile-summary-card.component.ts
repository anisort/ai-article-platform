import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { UserProfile } from '../../models/user-profile.model';
import { AvatarComponent } from '../../../../shared/components/avatar/avatar.component';

/**
 * Defines the connection tab type.
 */
type ConnectionTab = 'following' | 'followers' | null;

@Component({
  selector: 'app-profile-summary-card',
  standalone: true,
  imports: [CommonModule, AvatarComponent, RouterLink],
  templateUrl: './profile-summary-card.component.html',
  styleUrl: './profile-summary-card.component.scss',
})
/**
 * Defines the profile summary card component class.
 */
export class ProfileSummaryCardComponent {
  readonly profile = input.required<UserProfile>();
  readonly connectionTab = input<ConnectionTab>(null);
  readonly followLoading = input(false);
  readonly canFollow = input(false);
  readonly showStats = input(true);
  readonly settingsLink = input<string | null>(null);

  readonly connectionSelect = output<'following' | 'followers'>();
  readonly followToggle = output<void>();
  readonly backToArticles = output<void>();

/**
 * Performs the display name operation.
 * @returns the operation result
 */
  displayName(): string {
    const profile = this.profile();
    return profile.name || profile.username || 'Unknown author';
  }

/**
 * Performs the profile handle operation.
 * @returns the operation result
 */
  profileHandle(): string | null {
    const username = this.profile().username?.trim();
    return username ? `@${username}` : null;
  }

/**
 * Performs the followers count operation.
 * @returns the operation result
 */
  followersCount(): number {
    return this.profile().followers.length;
  }

/**
 * Performs the following count operation.
 * @returns the operation result
 */
  followingCount(): number {
    return this.profile().followingUsers.length;
  }
}
