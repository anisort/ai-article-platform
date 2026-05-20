import {inject, Injectable} from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import {authConfig} from '../config/auth-config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private oauthService = inject(OAuthService);

  async init() {
    this.oauthService.setStorage(localStorage);
    this.oauthService.configure(authConfig);
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  login() {
    this.oauthService.initLoginFlow();
  }

  logout() {
    this.oauthService.logOut();
  }

  isAuthenticated() {
    return this.oauthService.hasValidAccessToken();
  }

  get role() {
    const claims: any = this.oauthService.getIdentityClaims();
    return claims ? claims['role'] : null;
  }

  get token() {
    return this.oauthService.hasValidAccessToken()
      ? this.oauthService.getAccessToken()
      : null;
  }

  get userId(): string | null {
    const claims: any = this.oauthService.getIdentityClaims();
    if (claims?.sub) {
      return claims.sub;
    }

    const token = this.oauthService.getAccessToken();
    if (!token) {
      return null;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1] ?? ''));
      return payload?.sub ?? null;
    } catch {
      return null;
    }
  }
}
