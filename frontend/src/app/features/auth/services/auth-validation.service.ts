import { inject, Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, ValidationErrors, ValidatorFn } from '@angular/forms';
import { catchError, map, of, switchMap, timer } from 'rxjs';
import { UserRegisterService } from './user-register.service';

@Injectable({
  providedIn: 'root'
})
/**
 * Defines the auth validation service class.
 */
export class AuthValidationService {
  private readonly userRegisterService = inject(UserRegisterService);

/**
 * Performs the username availability validator operation.
 * @param ignoreUsername the ignoreUsername
 * @returns the operation result
 */
  usernameAvailabilityValidator(ignoreUsername?: () => string): AsyncValidatorFn {
    return (control: AbstractControl) => {
      const username = (control.value ?? '').trim();
      const ignored = (ignoreUsername?.() ?? '').trim();

      if (!username || control.hasError('minlength') || control.hasError('required')) {
        return of(null);
      }

      if (ignored && username.toLowerCase() === ignored.toLowerCase()) {
        return of(null);
      }

      return timer(250).pipe(
        switchMap(() => this.userRegisterService.checkUsernameAvailability(username)),
        map(response => response.available ? null : { usernameTaken: true }),
        catchError(() => of(null))
      );
    };
  }

/**
 * Performs the email availability validator operation.
 * @returns the operation result
 */
  emailAvailabilityValidator(): AsyncValidatorFn {
    return (control: AbstractControl) => {
      const email = (control.value ?? '').trim();
      if (!email || control.hasError('email') || control.hasError('required')) {
        return of(null);
      }

      return timer(250).pipe(
        switchMap(() => this.userRegisterService.checkEmailAvailability(email)),
        map(response => response.available ? null : { emailTaken: true }),
        catchError(() => of(null))
      );
    };
  }

/**
 * Performs the strong password pattern operation.
 * @returns the operation result
 */
  strongPasswordPattern(): RegExp {
    return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).+$/;
  }

/**
 * Performs the passwords match validator operation.
 * @param passwordKey the passwordKey
 * @param repeatPasswordKey the repeatPasswordKey
 * @returns the operation result
 */
  passwordsMatchValidator(passwordKey: string, repeatPasswordKey: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get(passwordKey)?.value;
      const repeatPassword = control.get(repeatPasswordKey)?.value;

      if (!password || !repeatPassword) {
        return null;
      }

      return password === repeatPassword ? null : { passwordMismatch: true };
    };
  }
}
