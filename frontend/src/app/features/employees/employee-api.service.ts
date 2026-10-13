import { Injectable, inject } from '@angular/core';
import { ApiBaseService } from '../../core/api-base.service';
import { ApiResponse } from './employee-api.types';
import { Employee, EmployeeRequest, PageResponse, ReferenceCountry, ReferenceDepartment } from './employee.models';

@Injectable({ providedIn: 'root' })
export class EmployeeApiService {
  private readonly api = inject(ApiBaseService);
  list(query: Record<string, string | number | undefined>) { return this.api.get<ApiResponse<PageResponse<Employee>>>('/employees', query); }
  countries() { return this.api.get<ApiResponse<ReferenceCountry[]>>('/countries'); }
  departments() { return this.api.get<ApiResponse<ReferenceDepartment[]>>('/departments'); }
  create(value: EmployeeRequest) { return this.api.post<ApiResponse<Employee>>('/employees', value); }
  update(id: string, value: EmployeeRequest) { return this.api.put<ApiResponse<Employee>>(`/employees/${id}`, value); }
  archive(id: string) { return this.api.delete<ApiResponse<null>>(`/employees/${id}`); }
  get(id:string) { return this.api.get<ApiResponse<Employee>>(`/employees/${id}`); }
}
