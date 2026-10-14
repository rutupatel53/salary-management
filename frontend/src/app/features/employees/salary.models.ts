import type { SalaryBreakdown } from '../salary/salary-workspace-api.service';
export interface SalaryVersion { id:string;effectiveDate:string;currencyCode:string;basic:number;allowances:number;deductions:number;gross:number;net:number;templateName?:string;templateRevision?:number;annualCtc?:number;assignmentBreakdown?:SalaryBreakdown; }
export interface SalaryChangeRequest { basic:string;allowances:string;deductions:string;currencyCode:string;effectiveDate:string; }
