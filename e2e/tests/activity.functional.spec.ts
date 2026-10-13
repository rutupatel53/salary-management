import { test, expect } from '@playwright/test';
import { ActivityPage } from '../pages/activity.page';

const apiURL = process.env.API_URL ?? 'http://127.0.0.1:8080/api';

test.beforeEach(async ({ request }) => {
  const response = await request.post(`${apiURL}/test/reset`);
  expect(response.ok()).toBeTruthy();
});

test('@functional activity feed shows successful salary changes newest first', async ({ page, request }) => {
  const list = await request.get(`${apiURL}/employees`, { params: { q: 'PW-001', page: 0, size: 10 } });
  const employee = (await list.json()).items[0];
  const change = await request.post(`${apiURL}/employees/${employee.id}/salary-history`, { data: {
    basic: '8500.00', allowances: '500.00', deductions: '300.00', currencyCode: 'INR', effectiveDate: '2026-01-01'
  } });
  expect(change.ok()).toBeTruthy();
  const activity = new ActivityPage(page); await activity.open(); await activity.expectLoaded();
  await expect(activity.rows().first()).toContainText('SALARY CHANGED');
  await expect(activity.rows().first()).toContainText(employee.employeeNumber);
});
