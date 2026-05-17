import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface UserInfoModel {
  sub?: string | null;
  id?: string | null;
  username?: string | null;
  name?: string | null;
  email?: string | null;
  avatar?: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class UserStoreService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlAuth}/v1/users/me`;
  private readonly subject = new BehaviorSubject<UserInfoModel | null>(null);

  readonly user$: Observable<UserInfoModel | null> = this.subject.asObservable();

  loadProfile(): Observable<UserInfoModel> {
    return this.http.get<UserInfoModel>(this.apiUrl).pipe(
      tap(user => this.subject.next(user))
    );
  }

  update(user: UserInfoModel): void {
    this.subject.next(user);
  }

  clear(): void {
    this.subject.next(null);
  }
}
