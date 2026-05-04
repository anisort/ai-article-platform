import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { AdvisorChatRequest, AdvisorChatResponse } from '../models/advisor-chat.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdvisorService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrlLlm}/v1/advisor`;

  chat(payload: AdvisorChatRequest): Observable<AdvisorChatResponse> {
    return this.http.post<AdvisorChatResponse>(`${this.apiUrl}/chat`, payload);
  }
}
