import { test, expect } from '@playwright/test';
import { ImportExportPage } from '../pages/import-export.page';
import path from 'node:path';
import { readFile } from 'node:fs/promises';
import { database } from '../support/database';

const apiURL = process.env.API_URL ?? 'http://127.0.0.1:8080/api';
const validFile = path.join(__dirname, '../fixtures/import-valid.xlsx');
const invalidFile = path.join(__dirname, '../fixtures/import-invalid.xlsx');

test.beforeEach(async ({ request }) => {
  const response = await request.post(`${apiURL}/test/reset`);
  expect(response.ok()).toBeTruthy();
});

test('@functional template downloads as an xlsx workbook', async ({ page }) => {
  const screen = new ImportExportPage(page); await screen.open();
  const downloadPromise = page.waitForEvent('download');
  await page.getByTestId('download-template').click();
  const download = await downloadPromise;
  expect(download.suggestedFilename()).toBe('salary-management-template.xlsx');
});

test('@functional valid spreadsheet previews then imports all rows', async ({ page, request }) => {
  const screen = new ImportExportPage(page); await screen.open(); await screen.upload(validFile); await screen.preview();
  await expect(page.getByTestId('import-preview')).toContainText('1 valid');
  await screen.confirm();
  const result = await request.get(`${apiURL}/employees`, { params: { q: 'PW-IMP-101', page: 0, size: 10 } });
  expect((await result.json()).totalItems).toBe(1);
  await expect(page.getByText('Import completed.')).toBeVisible();
});

test('@functional invalid spreadsheet shows row errors and cannot confirm', async ({ page }) => {
  const screen = new ImportExportPage(page); await screen.open(); await screen.upload(invalidFile); await screen.preview();
  await expect(page.getByTestId('import-row-2')).toContainText('Deductions cannot exceed gross pay');
  await expect(page.getByTestId('confirm-import')).toBeDisabled();
});

test('@functional filtered export downloads workbook', async ({ page }) => {
  const screen = new ImportExportPage(page); await screen.open();
  await page.getByTestId('export-search').fill('PW-001');
  const downloadPromise = page.waitForEvent('download'); await page.getByTestId('export-filtered').click();
  const download = await downloadPromise;
  expect(download.suggestedFilename()).toBe('salary-management-employees.xlsx');
});

test('@data confirmed import inserts exactly the rows in the workbook', async ({ request }) => {
  const content = await readFile(validFile);
  const response = await request.post(`${apiURL}/imports/confirm`, { multipart: { file: { name: 'import-valid.xlsx', mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', buffer: content } } });
  expect(response.ok()).toBeTruthy();
  expect((await response.json()).data.createdRows).toBe(1);
  const count = await database.query("SELECT count(*)::int AS count FROM employees WHERE employee_number='PW-IMP-101'");
  expect(count.rows[0].count).toBe(1);
});

test.afterAll(async () => { await database.end(); });
