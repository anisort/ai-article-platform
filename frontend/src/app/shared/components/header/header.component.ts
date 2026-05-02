import {Component, inject} from '@angular/core';
import {AuthService} from '../../../core/services/auth.service';
import {Router} from '@angular/router';
import {addIcons} from 'ionicons';
import {searchOutline} from 'ionicons/icons';
import {IonIcon, IonMenuButton} from '@ionic/angular/standalone';

@Component({
  selector: 'app-header',
  imports: [
    IonIcon,
    IonMenuButton
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  authService = inject(AuthService);
  router = inject(Router);

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
    this.authService.logout();
  }

  onProfile() {
    this.router.navigate(['/profile']);
  }
}
