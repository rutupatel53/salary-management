# Phase 7: test reporting, CI, and performance

Date: 2026-10-14

## Prompt used

Complete the required fast deterministic test suites and GitHub Actions pipeline. CI must run tests only, upload reports, and use Render/Netlify GitHub auto-deploy separately. Add a one-file HTML summary and capture PostgreSQL plans for the seeded 10,000-row workflows.

## What the AI got wrong or missed

- An existing Angular API spec represented salary values as numbers even though the exact-money request DTO uses strings. Corrected the test to send decimal strings.
- The Angular CLI unit-test builder reports no discovered spec files from this Windows workspace path containing spaces. The TypeScript specs compile, the production build passes, and the pure money tests pass directly; the Linux GitHub runner will validate the configured Angular unit-test command.
- The first JUnit-to-HTML report generator matched `name=` inside the `classname` attribute. Changed the attribute matcher to require a word boundary and verified distinct test names and failure messages with a sample JUnit report.

## Decisions followed

- Workflow jobs run Maven tests, Angular unit tests/build, and the tagged Playwright checks. There is no deployment job.
- Playwright reports HTML and JUnit, prints totals and failed-test reasons, and creates `reports/detailed-report.html`. Screenshots are captured on failure; trace and video are captured on retry.
- The 10,000-row performance script saves query plans for employee paging, country/currency summaries, and outlier bounds. No measured runtime is claimed until a PostgreSQL engine is available.

## Checks

- Maven: 19 tests passed, zero failures/errors.
- Angular production build: passed.
- Playwright TypeScript check: passed.
- Detailed report generator: passed a sample JUnit XML with one passing and one failing test.
- Full Angular unit and browser suites: not runnable in this local Windows path; Docker Engine is unavailable. GitHub Actions is configured to run them on Linux.
