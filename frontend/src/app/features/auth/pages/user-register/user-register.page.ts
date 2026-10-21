import {CommonModule} from '@angular/common';
import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {UserRegisterService} from '../../services/user-register.service';
import {AuthService} from '../../../../core/services/auth.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {finalize, tap} from 'rxjs';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageIntroComponent } from '../../../../shared/components/page-intro/page-intro.component';
import { AuthValidationService } from '../../services/auth-validation.service';

@Component({
  selector: 'app-user-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LoadingStateComponent,
    PageIntroComponent
  ],
  templateUrl: './user-register.page.html',
  styleUrl: './user-register.page.scss'
})
/**
 * Defines the user register page class.
 */
export class UserRegisterPage implements OnInit {
  registerForm!: FormGroup;
  loading = signal<boolean>(false);
  message = signal<string | undefined>(undefined);
  submitted = signal<boolean>(false);

  private userRegisterService = inject(UserRegisterService);
  private authService = inject(AuthService);
  private authValidationService = inject(AuthValidationService);
  private destroyRef = inject(DestroyRef);

/**
 * Performs the ng on init operation.
 */
  ngOnInit() {
    this.initForm();
  }

/**
 * Performs the init form operation.
 */
  initForm() {
    this.registerForm = new FormGroup({
        username: new FormControl('', {
          validators: [Validators.required, Validators.minLength(3)],
          asyncValidators: [this.authValidationService.usernameAvailabilityValidator()]
        }),
        email: new FormControl('', {
          validators: [Validators.required, Validators.email],
          asyncValidators: [this.authValidationService.emailAvailabilityValidator()]
        }),
        name: new FormControl('', [Validators.required, Validators.minLength(3)]),
        password: new FormControl('', [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(this.authValidationService.strongPasswordPattern())
        ]),
        repeatPassword: new FormControl('', [Validators.required])
      },
      { validators: this.authValidationService.passwordsMatchValidator('password', 'repeatPassword') }
    )
  }

/**
 * Performs the on register operation.
 */
  onRegister() {
    this.submitted.set(true);
    if (!this.registerForm.valid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);

    this.userRegisterService.register(this.registerForm.value)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap(() => {
          this.message.set("Successfully registered");
          this.registerForm.reset();
          this.submitted.set(false);
        }),
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        error: () => this.message.set("Error to register")
      });
  }

/**
 * Performs the on login operation.
 */
  onLogin() {
    this.authService.login();
  }

/**
 * Performs the has control error operation.
 * @param controlName the controlName
 * @param errorKey the errorKey
 * @returns the operation result
 */
  hasControlError(controlName: string, errorKey: string): boolean {
    const control = this.registerForm.get(controlName);
    return !!control && control.hasError(errorKey) && (control.dirty || this.submitted());
  }

/**
 * Performs the show password mismatch error operation.
 * @returns the operation result
 */
  showPasswordMismatchError(): boolean {
    const repeatPasswordControl = this.registerForm.get('repeatPassword');
    return !!repeatPasswordControl
      && (repeatPasswordControl.dirty || this.submitted())
      && this.registerForm.hasError('passwordMismatch');
  }
}
