import { defineConfig, devices } from '@playwright/test';
const baseURL = process.env.BASE_URL ?? 'http://127.0.0.1:4200';
export default defineConfig({
  testDir: './tests', fullyParallel: true, forbidOnly: !!process.env.CI, retries: process.env.CI ? 1 : 0,
  reporter: [['html', { outputFolder: 'reports/html', open: 'never' }], ['junit', { outputFile: 'reports/junit/results.xml' }]],
  use: { baseURL, trace: 'on-first-retry', screenshot: 'only-on-failure', video: 'on-first-retry', ...devices['Desktop Chrome'] },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }]
});
