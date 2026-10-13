import { expect, Page } from '@playwright/test';
export class ActivityPage {
  constructor(private readonly page: Page) { }
  async open(): Promise<void> { await this.page.goto('/activity'); }
  async expectLoaded(): Promise<void> { await expect(this.page.getByRole('heading', { name: 'Activity' })).toBeVisible(); }
  rows() { return this.page.locator('[data-testid^="activity-row-"]'); }
}
