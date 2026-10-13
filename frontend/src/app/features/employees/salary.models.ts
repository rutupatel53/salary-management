export interface SalaryVersion { id:string;effectiveDate:string;currencyCode:string;basic:number;allowances:number;deductions:number;gross:number;net:number; }
export interface SalaryChangeRequest { basic:string;allowances:string;deductions:string;currencyCode:string;effectiveDate:string; }
