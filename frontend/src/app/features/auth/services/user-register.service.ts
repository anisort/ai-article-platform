import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {Observable} from 'rxjs';
import {UserResponse} from '../models/user-response.model';
import {CreateUserRequest} from '../models/create-user-request.model';

@Injectable({
  providedIn: 'root'
})
export class UserRegisterService {
  private readonly apiUrl = `${environment.apiUrlAuth}/v1/users`;
  private readonly http = inject(HttpClient);

  register(registerUser: CreateUserRequest) : Observable<UserResponse> {
    return this.http.post<UserResponse>(this.apiUrl, registerUser);
  }
}
