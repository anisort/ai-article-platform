import {Component, inject, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {AuthService} from './core/services/auth.service';
import {HeaderComponent} from './shared/components/header/header.component';
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
  imports: [RouterOutlet, HeaderComponent, IonApp, IonMenu, IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonItem, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'frontend';

  authService = inject(AuthService);

  async ngOnInit(): Promise<void> {
    await this.authService.init();
    console.log('AuthService initialized and login attempt made.');
  }
}
