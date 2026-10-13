import { test, expect } from '@playwright/test';
import { EmployeeListPage } from '../pages/employee-list.page';

test('@sanity API health endpoint responds', async ({ request }) => {
  const apiURL = process.env.API_URL ?? 'http://127.0.0.1:8080/api';
  const response = await request.get(`${apiURL}/health`);
  expect(response.ok()).toBeTruthy();
  await expect(response).toBeOK();
  expect(await response.json()).toMatchObject({ status: 'UP' });
});

test('@sanity home page loads and employee list contains seeded rows', async ({ page }) => {
  const employees = new EmployeeListPage(page);
  await employees.open();
  await employees.expectLoaded();
  await expect(employees.rows().first()).toBeVisible();
});
