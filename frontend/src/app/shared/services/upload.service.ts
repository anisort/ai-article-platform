import {inject, Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {FileResponse} from '../models/file-response.model';

@Injectable({
  providedIn: 'root'
})
/**
 * Defines the upload service class.
 */
export class UploadService {
  private readonly apiUrl = `${environment.apiUrlUpload}/v1/files`;
  private readonly http = inject(HttpClient);

/**
 * Performs the upload file operation.
 * @param file the file
 * @returns the operation result
 */
  uploadFile(file: File): Observable<FileResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FileResponse>(this.apiUrl, formData);
  }
}
