import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { ModalController } from '@ionic/angular/standalone';
import { ModalLayoutComponent } from '../../../../shared/components/modal-layout/modal-layout.component';

import { UserSummary } from '../../models/user-profile.model';
import { ProfileConnectionCardComponent } from '../profile-connection-card/profile-connection-card.component';

/**
 * Defines the connection tab type.
 */
type ConnectionTab = 'following' | 'followers';

@Component({
  selector: 'app-profile-connections-modal',
  standalone: true,
  imports: [CommonModule, ModalLayoutComponent, ProfileConnectionCardComponent],
  templateUrl: './profile-connections-modal.component.html',
  styleUrl: './profile-connections-modal.component.scss',
})
/**
 * Defines the profile connections modal component class.
 */
export class ProfileConnectionsModalComponent {
  private readonly modalController = inject(ModalController);

  @Input({ required: true }) tab!: ConnectionTab;
  @Input() users: UserSummary[] = [];
  searchValue = '';

/**
 * Performs the title operation.
 * @returns the operation result
 */
  title(): string {
    return this.tab === 'following' ? 'Following' : 'Followers';
  }

/**
 * Performs the empty message operation.
 * @returns the operation result
 */
  emptyMessage(): string {
    return this.tab === 'following' ? 'No subscriptions yet.' : 'No followers yet.';
  }

/**
 * Performs the filtered users operation.
 * @returns the operation result
 */
  filteredUsers(): UserSummary[] {
    const query = this.searchValue.trim().toLowerCase();
    if (!query) {
      return this.users;
    }

    return this.users.filter(user => {
      const name = user.name?.toLowerCase() ?? '';
      const email = user.email?.toLowerCase() ?? '';
      const username = user.username?.toLowerCase() ?? '';

      return name.includes(query) || email.includes(query) || username.includes(query);
    });
  }

/**
 * Performs the has users operation.
 * @returns the operation result
 */
  hasUsers(): boolean {
    return this.users.length > 0;
  }

/**
 * Performs the on search value change operation.
 * @param value the value
 */
  onSearchValueChange(value: string): void {
    this.searchValue = value;
  }

/**
 * Performs the close operation.
 */
  close(): void {
    this.modalController.dismiss();
  }
}
