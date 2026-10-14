import { test, expect } from '@playwright/test';

test('@functional mobile navigation closes after choosing a page and keeps content in viewport', async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto('/employees');
  await expect(page.getByTestId('employee-table')).toBeVisible();
  await expect(page.getByTestId('toggle-navigation')).toHaveAttribute('aria-expanded', 'false');
  await page.getByTestId('toggle-navigation').click();
  await expect(page.getByRole('navigation', { name: 'Main navigation' })).toBeVisible();
  await page.getByTestId('nav-insights').click();
  await expect(page.getByRole('heading', { name: 'Pay insights', exact: true })).toBeVisible();
  await expect(page.getByTestId('toggle-navigation')).toHaveAttribute('aria-expanded', 'false');
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
  await page.getByTestId('toggle-navigation').click();
  await expect(page.getByTestId('nav-insights')).toHaveAttribute('aria-current', 'page');
  await page.keyboard.press('Escape');
  await expect(page.getByTestId('toggle-navigation')).toHaveAttribute('aria-expanded', 'false');
});

test('@functional desktop navigation identifies the current page and tables stay within the workspace', async ({ page }) => {
  await page.setViewportSize({ width: 1280, height: 900 });
  await page.goto('/employees');
  await expect(page.getByTestId('employee-table')).toBeVisible();
  await expect(page.getByTestId('nav-employees')).toHaveAttribute('aria-current', 'page');
  await expect(page.getByTestId('status-badge').filter({ hasText: 'Active employees' })).toBeVisible();
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});
