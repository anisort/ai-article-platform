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
/**
 * Defines the header component class.
 */
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

/**
 * Performs the constructor operation.
 */
  constructor() {
    addIcons({ searchOutline })
  }

/**
 * Performs the on register operation.
 */
  onRegister() {
    this.router.navigate(['/register']);
  }

/**
 * Performs the on login operation.
 */
  onLogin() {
    this.authService.login();
  }

/**
 * Performs the on logout operation.
 */
  onLogout() {
    this.userStore.clear();
    this.authService.logout();
  }

/**
 * Performs the on search submit operation.
 * @param query the query
 */
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
