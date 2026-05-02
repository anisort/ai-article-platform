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
export class ProfileConnectionCardComponent {
  readonly user = input.required<UserSummary>();

  displayName(): string {
    const user = this.user();
    return user.name || user.username;
  }
}
