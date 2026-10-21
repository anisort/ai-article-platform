import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.scss',
})
/**
 * Defines the avatar component class.
 */
export class AvatarComponent {
  readonly imageUrl = input<string | null | undefined>(null);
  readonly name = input<string | null | undefined>(null);
  readonly size = input<string>('4.25rem');

  readonly initials = computed(() => {
    const value = (this.name() ?? '').trim();
    if (!value) {
      return '?';
    }

    const parts = value.split(/\s+/).filter(Boolean);
    if (parts.length === 1) {
      return parts[0].slice(0, 1).toUpperCase();
    }

    return `${parts[0][0] ?? ''}${parts[1][0] ?? ''}`.toUpperCase();
  });
}
