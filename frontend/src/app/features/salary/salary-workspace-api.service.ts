import { Injectable, inject } from '@angular/core';
import { ApiBaseService } from '../../core/api-base.service';
import { ApiResponse } from '../employees/employee-api.types';
import { Employee, PageResponse } from '../employees/employee.models';
import { SalaryVersion } from '../employees/salary.models';

export type SalaryTemplateStatus = 'DRAFT' | 'ACTIVE' | 'ARCHIVED';
export type SalaryComponentCategory = 'BASIC' | 'ALLOWANCE' | 'EMPLOYER_CONTRIBUTION' | 'DEDUCTION';
export type SalaryComponentCalculation = 'FIXED' | 'PERCENT_CTC' | 'PERCENT_BASIC' | 'BALANCE';
export interface TemplateComponent { name: string; category: SalaryComponentCategory; calculation: SalaryComponentCalculation; value: number; }
export interface SalaryTemplate { id: string; name: string; countryCode: string; currencyCode: string; status: SalaryTemplateStatus; revision: number; components: TemplateComponent[]; }
export interface AssignmentRequest { employeeId: string; templateId: string; templateRevision: number; annualCtc: string; effectiveDate: string; }
export interface SalaryTotals { ctc: number; basic: number; allowances: number; employerContributions: number; deductions: number; gross: number; net: number; }
export interface SalaryComponentAmount { name: string; category: SalaryComponentCategory; annual: number; monthly: number; }
export interface SalaryBreakdown { annual: SalaryTotals; monthly: SalaryTotals; components: SalaryComponentAmount[]; }
export interface AssignmentPreview { employee: Employee; template: SalaryTemplate; previousSalary: SalaryVersion | null; breakdown: SalaryBreakdown; }
@Injectable({ providedIn: 'root' })
export class SalaryWorkspaceApiService {
  private readonly api = inject(ApiBaseService);
  list(params: Record<string, string | number>) { return this.api.get<ApiResponse<PageResponse<SalaryTemplate>>>('/salary-templates', params); }
  save(template: Partial<SalaryTemplate>) { return template.id ? this.api.put<ApiResponse<SalaryTemplate>>(`/salary-templates/${template.id}`, template) : this.api.post<ApiResponse<SalaryTemplate>>('/salary-templates', template); }
  preview(request: AssignmentRequest) { return this.api.post<ApiResponse<AssignmentPreview>>('/salary-assignments/preview', request); }
  assign(request: AssignmentRequest) { return this.api.post<ApiResponse<SalaryVersion>>('/salary-assignments', request); }
}
