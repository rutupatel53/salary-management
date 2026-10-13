export interface Employee {
  id: string; employeeNumber: string; firstName: string; lastName: string; workEmail: string;
  hireDate: string; countryCode: string; countryName: string; departmentId: string;
  departmentName: string; jobTitle: string; currencyCode:string;
}
export interface EmployeeRequest {
  employeeNumber: string; firstName: string; lastName: string; workEmail: string; hireDate: string;
  countryCode: string; departmentId: string; jobTitle: string;
}
export interface PageResponse<T> { items: T[]; page: number; size: number; totalItems: number; totalPages: number; }
export interface ReferenceCountry { countryCode: string; name: string; currencyCode: string; }
export interface ReferenceDepartment { id: string; name: string; }
