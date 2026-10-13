import { expect, Page } from '@playwright/test';
export class InsightsPage {
  constructor(private readonly page: Page) { }
  async open(): Promise<void> { await this.page.goto('/insights'); }
  async expectLoaded(): Promise<void> { await expect(this.page.getByRole('heading', { name: /pay insights/i })).toBeVisible(); }
 async filterCountry(countryCode:string):Promise<void>{const name:Record<string,string>={US:'United States',IN:'India',GB:'United Kingdom',DE:'Germany',CA:'Canada'};await this.page.getByTestId('insights-country').click();await this.page.getByRole('option',{name:name[countryCode]}).click();}
}
