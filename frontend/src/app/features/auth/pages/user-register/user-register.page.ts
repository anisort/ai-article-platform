import {CommonModule} from '@angular/common';
import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserRegisterService} from '../../services/user-register.service';
import {AuthService} from '../../../../core/services/auth.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {finalize, tap} from 'rxjs';

@Component({
  selector: 'app-user-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './user-register.page.html',
  styleUrl: './user-register.page.scss'
})
export class UserRegisterPage implements OnInit {
  registerForm!: FormGroup;
  loading = signal<boolean>(false);
  message = signal<string | undefined>(undefined);

  private userRegisterService = inject(UserRegisterService);
  private authService = inject(AuthService);
  private destroyRef = inject(DestroyRef);

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.registerForm = new FormGroup({
        username: new FormControl('', [Validators.required, Validators.minLength(3)]),
        email: new FormControl('', [Validators.required, Validators.email]),
        name: new FormControl('', [Validators.required, Validators.minLength(3)]),
        password: new FormControl('', [Validators.required, Validators.minLength(8)]),
        repeatPassword: new FormControl('', [Validators.required])
      },
    )
  }

  onRegister() {
    if (!this.registerForm.valid) return;

    this.loading.set(true);

    this.userRegisterService.register(this.registerForm.value)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap(() => {
          this.message.set("Successfully registered");
          this.registerForm.reset();
        }),
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        error: () => this.message.set("Error to register")
      });
  }

  onLogin() {
    this.authService.login();
  }
}
