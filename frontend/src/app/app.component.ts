import {DOCUMENT} from '@angular/common';
import {Component, inject, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {AuthService} from './core/services/auth.service';
import { UserStoreService } from './core/services/user-store.service';
import {HeaderComponent} from './shared/components/header/header.component';
import {AdvisorWidgetComponent} from './features/advisor/components/advisor-widget/advisor-widget.component';
import {
  IonApp,
  IonContent,
  IonHeader,
  IonItem,
  IonList,
  IonMenu,
  IonTitle,
  IonToolbar
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, AdvisorWidgetComponent, IonApp, IonMenu, IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  private readonly document = inject(DOCUMENT);
  private readonly themeStorageKey = 'app-theme';

  title = 'frontend';
  isDarkTheme = false;

  authService = inject(AuthService);
  userStore = inject(UserStoreService);

  async ngOnInit(): Promise<void> {
    this.initializeTheme();
    await this.authService.init();
    if (this.authService.isAuthenticated()) {
      this.userStore.loadProfile().subscribe({
        error: () => this.userStore.clear(),
      });
    } else {
      this.userStore.clear();
    }
  }

  toggleTheme(): void {
    this.isDarkTheme = !this.isDarkTheme;
    this.applyTheme(this.isDarkTheme);
  }

  private initializeTheme(): void {
    const storedTheme = localStorage.getItem(this.themeStorageKey);
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

    this.isDarkTheme = storedTheme ? storedTheme === 'dark' : prefersDark;
    this.applyTheme(this.isDarkTheme, false);
  }

  private applyTheme(isDarkTheme: boolean, persist = true): void {
    this.document.body.classList.toggle('theme-dark', isDarkTheme);
    this.document.cookie = `app-theme=${isDarkTheme ? 'dark' : 'light'}; Path=/; Max-Age=31536000; SameSite=Lax`;

    if (persist) {
      localStorage.setItem(this.themeStorageKey, isDarkTheme ? 'dark' : 'light');
    }
  }
}
