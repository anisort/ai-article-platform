import {AuthConfig} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.oauthIssuer,
  redirectUri: 'http://localhost:4200/articles',
  clientId: environment.oauthClientId,
  dummyClientSecret: environment.oauthSecret,
  responseType: 'code',
  scope: 'openid profile',
  showDebugInformation: true,
  requireHttps: false,
  clearHashAfterLogin: true,
  disablePKCE: false,
};
