import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { UserSummary } from '../../models/user-profile.model';
import { AvatarComponent } from '../../../../shared/components/avatar/avatar.component';

@Component({
  selector: 'app-profile-connection-card',
  standalone: true,
  imports: [CommonModule, RouterLink, AvatarComponent],
  templateUrl: './profile-connection-card.component.html',
  styleUrl: './profile-connection-card.component.scss',
})
/**
 * Defines the profile connection card component class.
 */
export class ProfileConnectionCardComponent {
  readonly user = input.required<UserSummary>();

/**
 * Performs the display name operation.
 * @returns the operation result
 */
  displayName(): string {
    const user = this.user();
    return user.name || user.username || 'Unknown author';
  }

/**
 * Performs the profile handle operation.
 * @returns the operation result
 */
  profileHandle(): string | null {
    const username = this.user().username?.trim();
    return username ? `@${username}` : null;
  }

/**
 * Performs the email operation.
 * @returns the operation result
 */
  email(): string | null {
    const email = this.user().email?.trim();
    return email ? email : null;
  }
}
