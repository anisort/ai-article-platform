import {Component, computed, inject} from '@angular/core';
import {AuthService} from '../../../core/services/auth.service';
import {Router} from '@angular/router';
import { RouterLink } from '@angular/router';
import {addIcons} from 'ionicons';
import {searchOutline} from 'ionicons/icons';
import {IonIcon, IonMenuButton} from '@ionic/angular/standalone';
import { toSignal } from '@angular/core/rxjs-interop';
import { UserStoreService } from '../../../core/services/user-store.service';
import { AvatarComponent } from '../avatar/avatar.component';

@Component({
  selector: 'app-header',
  imports: [
    IonIcon,
    IonMenuButton,
    AvatarComponent,
    RouterLink
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  authService = inject(AuthService);
  router = inject(Router);
  userStore = inject(UserStoreService);
  searchQuery = '';
  readonly user = toSignal(this.userStore.user$, { initialValue: null });
  readonly userDisplay = computed(() => {
    const user = this.user();
    return user?.name || user?.username || user?.email || 'Profile';
  });

  constructor() {
    addIcons({ searchOutline })
  }

  onRegister() {
    this.router.navigate(['/register']);
  }

  onLogin() {
    this.authService.login();
  }

  onLogout() {
    this.userStore.clear();
    this.authService.logout();
  }

  onSearchSubmit(query: string) {
    this.searchQuery = query;
    this.router.navigate(['/articles'], {
      queryParams: {
        q: query.trim() || null,
      },
      queryParamsHandling: 'merge',
    });
  }
}
