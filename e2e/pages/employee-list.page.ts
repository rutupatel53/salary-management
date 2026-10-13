import { expect, Page } from '@playwright/test';
export class EmployeeListPage {
  constructor(private readonly page: Page) { }
  async open(): Promise<void> { await this.page.goto('/employees'); }
  async expectLoaded(): Promise<void> { await expect(this.page.getByRole('heading', { name: 'Employee directory' })).toBeVisible(); }
  rows() { return this.page.locator('[data-testid^="employee-row-"]'); }
  async search(value: string): Promise<void> { await this.page.getByTestId('employee-search').fill(value); await this.page.getByTestId('employee-search').press('Enter'); }
}
