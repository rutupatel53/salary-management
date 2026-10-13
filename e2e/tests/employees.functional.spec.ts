import { test, expect } from '@playwright/test';

const apiURL = process.env.API_URL ?? 'http://127.0.0.1:8080/api';

test.beforeEach(async ({ request }) => {
  const reset = await request.post(`${apiURL}/test/reset`);
  expect(reset.ok()).toBeTruthy();
});

test('@functional employee can be created, edited, searched, and archived', async ({ page, request }) => {
  await page.goto('/employees');
  await page.getByTestId('add-employee').click();
  await page.getByTestId('employee-form-number').fill('PW-E2E-901');
  await page.getByTestId('employee-form-first-name').fill('Ari');
  await page.getByTestId('employee-form-last-name').fill('Example');
  await page.getByTestId('employee-form-email').fill('pw-e2e-901@example.test');
  await page.getByTestId('employee-form-hire-date').fill('2024-03-12');
  await page.getByTestId('employee-form-country').click();
  await page.getByRole('option', { name: 'India (INR)' }).click();
  await page.getByTestId('employee-form-department').click();
  await page.getByRole('option', { name: 'Engineering' }).click();
  await page.getByTestId('employee-form-title').fill('Test Engineer');
  await page.getByTestId('save-employee').click();
  await expect(page.getByRole('dialog', { name: /employee/i })).toHaveCount(0);

  await page.getByTestId('employee-search').fill('PW-E2E-901');
  await page.getByTestId('employee-search').press('Enter');
  const row = page.getByTestId('employee-row-PW-E2E-901');
  await expect(row).toContainText('Ari Example');

  await page.getByTestId('edit-PW-E2E-901').click();
  await page.getByTestId('employee-form-first-name').fill('Arianna');
  await page.getByTestId('save-employee').click();
  await expect(page.getByRole('dialog', { name: /employee/i })).toHaveCount(0);
  await expect(row).toContainText('Arianna Example');

  await page.getByTestId('archive-PW-E2E-901').click();
  await expect(page.getByTestId('confirm-title')).toHaveText('Archive employee');
  await page.getByTestId('confirm-action').click();
  await expect(row).toHaveCount(0);
  const archived = await request.get(`${apiURL}/employees?q=PW-E2E-901&page=0&size=10`);
  expect((await archived.json()).data.totalItems).toBe(0);
});


test('@functional employee modal validates required fields and confirms discarded changes', async ({ page }) => {
  await page.goto('/employees');
  await page.getByTestId('add-employee').click();
  const dialog = page.getByRole('dialog', { name: 'Add employee', exact: true });
  await expect(dialog).toBeVisible();
  await page.getByTestId('save-employee').click();
  await expect(dialog.getByText('Enter a valid employee number.')).toBeVisible();
  await page.getByTestId('employee-form-first-name').fill('Unsaved');
  await page.getByTestId('cancel-employee').click();
  await expect(page.getByTestId('confirm-title')).toHaveText('Discard changes?');
  await page.getByTestId('cancel-action').click();
  await expect(page.getByTestId('employee-form-first-name')).toHaveValue('Unsaved');
  await page.getByTestId('cancel-employee').click();
  await page.getByTestId('confirm-action').click();
  await expect(dialog).toHaveCount(0);
  await expect(page.getByTestId('add-employee')).toBeFocused();
});
