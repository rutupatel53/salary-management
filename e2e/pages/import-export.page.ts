import { Page } from '@playwright/test';
export class ImportExportPage {
  constructor(private readonly page: Page) { }
  async open(): Promise<void> { await this.page.goto('/import-export'); }
  async upload(filePath: string): Promise<void> { await this.page.getByTestId('import-file').setInputFiles(filePath); }
  async preview(): Promise<void> { await this.page.getByTestId('preview-import').click(); }
  async confirm(): Promise<void> { await this.page.getByTestId('confirm-import').click(); }
}
