import { Injectable, inject } from '@angular/core';
import { ApiBaseService } from '../../core/api-base.service';
import { ApiResponse } from '../employees/employee-api.types';
export interface Activity { id: number; occurredAt: string; actor: string; action: string; employeeId: string | null; details: Record<string, unknown>; }
export interface ActivityPage { items: Activity[]; page: number; size: number; totalItems: number; totalPages: number; }
@Injectable({ providedIn: 'root' })
export class ActivityApiService {
  private readonly api = inject(ApiBaseService);
  list(page: number, size: number, action?: string) { return this.api.get<ApiResponse<ActivityPage>>('/activity', { page, size, action }); }
}
