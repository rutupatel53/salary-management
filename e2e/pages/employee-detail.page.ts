import { Page } from '@playwright/test';
export class EmployeeDetailPage {
  constructor(private readonly page: Page) { }
  async openSalaryAssignment(): Promise<void> { await this.page.getByTestId('detail-assign-salary').click(); }
}
