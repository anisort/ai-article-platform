import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ProfileService } from '../../services/profile.service';
import { UserProfile } from '../../models/user-profile.model';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import { AvatarComponent } from '../../../../shared/components/avatar/avatar.component';
import { AuthValidationService } from '../../../auth/services/auth-validation.service';

@Component({
  selector: 'app-edit-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PageIntroComponent, AvatarComponent],
  templateUrl: './edit-profile.page.html',
  styleUrl: './edit-profile.page.scss'
})
export class EditProfilePage implements OnInit, OnDestroy {
  private readonly profileService = inject(ProfileService);
  private readonly authValidationService = inject(AuthValidationService);
  private readonly router = inject(Router);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly submitted = signal(false);
  readonly message = signal<string | null>(null);
  readonly avatarPreview = signal<string | null>(null);
  selectedAvatar: File | null = null;
  currentProfile: UserProfile | null = null;
  private avatarObjectUrl: string | null = null;

  readonly form = new FormGroup({
    username: new FormControl('', {
      validators: [Validators.required, Validators.minLength(3)],
      asyncValidators: [this.authValidationService.usernameAvailabilityValidator(() => this.currentProfile?.username ?? '')]
    }),
    name: new FormControl('', [Validators.required, Validators.minLength(2)])
  });

  ngOnInit(): void {
    this.profileService.getProfilePage().subscribe({
      next: response => {
        this.currentProfile = response.profile;
        this.form.patchValue({
          username: response.profile.username,
          name: response.profile.name
        });
        this.avatarPreview.set(response.profile.avatar ?? null);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.message.set('Failed to load profile.');
      }
    });
  }

  ngOnDestroy(): void {
    this.revokeAvatarObjectUrl();
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.selectedAvatar = file;

    if (!file) {
      this.revokeAvatarObjectUrl();
      this.avatarPreview.set(this.currentProfile?.avatar ?? null);
      return;
    }

    this.revokeAvatarObjectUrl();
    this.avatarObjectUrl = URL.createObjectURL(file);
    this.avatarPreview.set(this.avatarObjectUrl);
  }

  onSubmit(): void {
    this.submitted.set(true);
    if (this.form.invalid || this.saving()) {
      this.form.markAllAsTouched();
      return;
    }

    const username = (this.form.value.username ?? '').trim();
    const name = (this.form.value.name ?? '').trim();
    this.saving.set(true);
    this.message.set(null);

    this.profileService.updateOwnProfile(username, name, this.selectedAvatar)
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => this.router.navigate(['/profile']),
        error: (err) => {
          if (err?.status === 409) {
            this.message.set('Username is already taken.');
          } else {
            this.message.set('Failed to update profile.');
          }
        }
      });
  }

  private revokeAvatarObjectUrl(): void {
    if (!this.avatarObjectUrl) {
      return;
    }

    URL.revokeObjectURL(this.avatarObjectUrl);
    this.avatarObjectUrl = null;
  }
}
