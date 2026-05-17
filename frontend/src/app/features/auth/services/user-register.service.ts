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
export class UserRegisterService {
  private readonly apiUrl = `${environment.apiUrlAuth}/v1/users`;
  private readonly http = inject(HttpClient);

  register(registerUser: CreateUserRequest) : Observable<UserResponse> {
    return this.http.post<UserResponse>(this.apiUrl, registerUser);
  }

  checkUsernameAvailability(username: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${this.apiUrl}/availability/username`, {
      params: { username }
    });
  }

  checkEmailAvailability(email: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${this.apiUrl}/availability/email`, {
      params: { email }
    });
  }
}
