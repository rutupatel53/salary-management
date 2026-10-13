import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_BASE = (globalThis as { salaryApiBase?: string }).salaryApiBase ?? '/api';

@Injectable({ providedIn: 'root' })
export class ApiBaseService {
  private readonly http = inject(HttpClient);
  get<T>(path: string, params?: Record<string, string | number | undefined>): Observable<T> {
    let query = new HttpParams();
    for (const [key, value] of Object.entries(params ?? {})) if (value !== undefined && value !== '') query = query.set(key, value);
    return this.http.get<T>(`${API_BASE}${path}`, { params: query });
  }
  post<T>(path: string, body: unknown): Observable<T> { return this.http.post<T>(`${API_BASE}${path}`, body); }
  put<T>(path: string, body: unknown): Observable<T> { return this.http.put<T>(`${API_BASE}${path}`, body); }
  delete<T>(path: string): Observable<T> { return this.http.delete<T>(`${API_BASE}${path}`); }
}
