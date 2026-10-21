import {inject, Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {AuthService} from '../services/auth.service';

/**
 * Defines the auth interceptor class.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  authService = inject(AuthService);

/**
 * Performs the intercept operation.
 * @param req the req
 * @param next the next
 * @returns the operation result
 */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.token;
    const authReq = token ? req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    }) : req;

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !!token) {
          this.authService.logout();
          this.authService.login();
        }
        return throwError(() => error);
      })
    );
  }
}
