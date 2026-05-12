import {AuthConfig} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.oauthIssuer,
  redirectUri: 'http://localhost:4200/home',
  clientId: environment.oauthClientId,
  responseType: 'code',
  scope: 'openid profile',
  showDebugInformation: true,
  requireHttps: false,
  clearHashAfterLogin: true,
  disablePKCE: false,
};
