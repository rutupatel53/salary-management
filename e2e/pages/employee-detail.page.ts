import { Page } from '@playwright/test';
export class EmployeeDetailPage {
  constructor(private readonly page: Page) { }
  async edit(employeeNumber: string): Promise<void> { await this.page.getByTestId(`edit-${employeeNumber}`).click(); }
 async changeSalary(basic:string,allowances:string,deductions:string,effectiveDate:string):Promise<void>{
  await this.page.getByTestId('salary-basic').fill(basic);await this.page.getByTestId('salary-allowances').fill(allowances);
  await this.page.getByTestId('salary-deductions').fill(deductions);await this.page.getByTestId('salary-effective-date').fill(effectiveDate);
  await this.page.getByTestId('save-salary').click();
 }
}
