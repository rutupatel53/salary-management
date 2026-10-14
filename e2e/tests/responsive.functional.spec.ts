import { test, expect } from '@playwright/test';

test('@functional mobile hamburger opens the side menu and closes after navigation', async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto('/employees');
  await expect(page.getByTestId('employee-table')).toBeVisible();
  await expect(page.getByTestId('toggle-navigation')).toHaveAttribute('aria-expanded', 'false');
  await page.getByTestId('toggle-navigation').click();
  const navigation = page.getByRole('navigation', { name: 'Main navigation' });
  await expect(navigation).toBeVisible();
  await expect(page.getByTestId('nav-activity')).toBeVisible();
  await page.getByTestId('nav-insights').click();
  await expect(page.getByRole('heading', { name: 'Pay insights', exact: true })).toBeVisible();
  await expect(page.getByTestId('toggle-navigation')).toHaveAttribute('aria-expanded', 'false');
  await expect(navigation).toBeHidden();
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});

test('@functional desktop hamburger opens the side menu and Escape closes it', async ({ page }) => {
  await page.setViewportSize({ width: 1280, height: 900 });
  await page.goto('/employees');
  await expect(page.getByTestId('employee-table')).toBeVisible();
  const navigation = page.getByRole('navigation', { name: 'Main navigation' });
  await expect(navigation).toBeHidden();
  await page.getByTestId('toggle-navigation').click();
  await expect(navigation).toBeVisible();
  await page.keyboard.press('Escape');
  await expect(page.getByTestId('toggle-navigation')).toHaveAttribute('aria-expanded', 'false');
  await expect(navigation).toBeHidden();
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});
