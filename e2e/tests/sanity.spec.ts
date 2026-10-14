import { test, expect } from '@playwright/test';
import { EmployeeListPage } from '../pages/employee-list.page';
import { InsightsPage } from '../pages/insights.page';

test('@sanity API health endpoint responds', async ({ request }) => {
  const apiURL = process.env.API_URL ?? 'http://127.0.0.1:8080/api';
  const response = await request.get(`${apiURL}/health`);
  expect(response.ok()).toBeTruthy();
  await expect(response).toBeOK();
  expect(await response.json()).toMatchObject({ status: 'UP' });
});

test('@sanity home page loads and employee list contains seeded rows', async ({ page }) => {
  const pageErrors: string[] = [];
  page.on('pageerror', error => pageErrors.push(error.message));
  const runtimeConfig = page.waitForResponse(response => response.url().endsWith('/runtime-config.js'));
  const employees = new EmployeeListPage(page);
  await employees.open();
  const configResponse = await runtimeConfig;
  expect(configResponse.ok()).toBeTruthy();
  expect(configResponse.headers()['content-type']).toContain('javascript');
  await employees.expectLoaded();
  await expect(employees.rows().first()).toBeVisible();
  expect(pageErrors).toEqual([]);
});

test('@sanity insights screen loads without errors',async({page})=>{
 const insights=new InsightsPage(page);await insights.open();await insights.expectLoaded();
 await expect(page.getByTestId('pay-summary')).toBeVisible();await expect(page.getByTestId('pay-outliers')).toBeVisible();
 await expect(page.getByRole('alert')).toHaveCount(0);
});
