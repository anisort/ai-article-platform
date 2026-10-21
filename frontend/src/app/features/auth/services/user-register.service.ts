import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {Observable} from 'rxjs';
import {UserResponse} from '../models/user-response.model';
import {CreateUserRequest} from '../models/create-user-request.model';
import {AvailabilityResponse} from '../models/availability-response.model';

@Injectable({
  providedIn: 'root'
})
/**
 * Defines the user register service class.
 */
export class UserRegisterService {
  private readonly apiUrl = `${environment.apiUrlAuth}/v1/users`;
  private readonly http = inject(HttpClient);

/**
 * Performs the register operation.
 * @param registerUser the registerUser
 * @returns the operation result
 */
  register(registerUser: CreateUserRequest) : Observable<UserResponse> {
    return this.http.post<UserResponse>(this.apiUrl, registerUser);
  }

/**
 * Performs the check username availability operation.
 * @param username the username
 * @returns the operation result
 */
  checkUsernameAvailability(username: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${this.apiUrl}/availability/username`, {
      params: { username }
    });
  }

/**
 * Performs the check email availability operation.
 * @param email the email
 * @returns the operation result
 */
  checkEmailAvailability(email: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${this.apiUrl}/availability/email`, {
      params: { email }
    });
  }
}
