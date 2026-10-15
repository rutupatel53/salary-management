# Final verification report

Date: 15 October 2026. Application baseline: `2f747d33dc5d37dd5dca894200c8be0071953926`.

Frontend: https://superlative-blancmange-ebe430.netlify.app

API: https://salary-management-1vd3.onrender.com

**Result: partial verification, not an all-green production release.** Live health, read-only screens, current data export, CORS, reset protection, local unit/build tests, and local PostgreSQL checks pass. Existing production browser suites have test-harness failures. Production SQL and production seed/restart evidence remain pending. No feature, test, or application configuration changes were made in this pass.

## Existing Playwright suites against the live URLs

The API_URL used by the suite is `https://salary-management-1vd3.onrender.com/api`; BASE_URL is the Netlify URL above. One Chromium worker, no retries. The repeated run produced the same 5/15 outcome. Latest JUnit duration: 90.459878 seconds.

| Suite | Executed | Passed | Failed | Skipped | Not executed / pending |
| --- | ---: | ---: | ---: | ---: | ---: |
| @sanity | 3 | 2 | 1 | 0 | 0 |
| @functional | 14 | 2 | 12 | 0 | 0 |
| @data | 3 | 1 | 2 | 0 | 3 |
| **Total** | **20** | **5** | **15** | **0** | **3** |

There are 23 tests across the requested tags. Three SQL-dependent tests were explicitly excluded, not reported as Playwright skips: seed count/uniqueness SQL; salary/reference SQL; country aggregates versus direct SQL. Production credentials were deliberately not used. The API-only outlier test passed. Two other data tests failed at reset setup before reaching their bodies.

Fourteen failures below are fixture compatibility failures, not evidence that those product features failed. They prevented create/edit/archive/import/assignment workflows from being exercised. Do not enable production reset support to make the tests pass.

### Failure names and reasons

| Test | Reason |
| --- | --- |
| @functional activity feed shows successful salary changes newest first | Fixture beforeEach expects reset success; production returns 404 |
| @functional employee can be created, edited, searched, and archived | Fixture beforeEach expects reset success; production returns 404 |
| @functional employee modal validates required fields and confirms discarded changes | Fixture beforeEach expects reset success; production returns 404 |
| @functional shared dropdown supports keyboard selection and filters employees | Fixture beforeEach expects reset success; production returns 404 |
| @functional employee sort stays on the server and preserves search | Fixture beforeEach expects reset success; production returns 404 |
| @functional template downloads as an xlsx workbook | Fixture beforeEach expects reset success; production returns 404 |
| @functional valid spreadsheet previews then imports all rows | Fixture beforeEach expects reset success; production returns 404 |
| @functional invalid spreadsheet shows row errors and cannot confirm | Fixture beforeEach expects reset success; production returns 404 |
| @functional filtered export downloads workbook | Fixture beforeEach expects reset success; production returns 404 |
| @data confirmed import inserts exactly the rows in the workbook | Fixture beforeEach expects reset success; production returns 404 |
| @functional insight average refreshes after a salary change | Fixture beforeEach expects reset success; production returns 404 |
| @functional HR creates a template and assigns salary directly by employee search | Fixture beforeEach expects reset success; production returns 404 |
| @functional assignment rejects a stale template and incompatible country | Fixture beforeEach expects reset success; production returns 404 |
| @data template edits preserve previously assigned component snapshots | Fixture beforeEach expects reset success; production returns 404 |
| @sanity home page loads and employee list contains seeded rows | Waits for removed runtime-config.js; 30-second timeout |

### Supplementary read-only browser check

All six main screens loaded with a visible page guide and no page errors or alert state: employees, salary templates, salary assignments, insights, import/export, and activity. Employee search `ACME-IN-J01-` returned 200 matching records. The directory table and INR insight summary were visible. These checks do not replace unexecuted write workflows and are not added to the Playwright suite counts.

## Live data evidence

| Check | Observed result | Scope |
| --- | --- | --- |
| Active employee count | 10,000 | Live API total and full filtered export |
| Active ACME seed count | 10,000 | Live API search |
| Duplicate employee numbers | 0 | 10,000 active export rows |
| Duplicate case-insensitive emails | 0 | 10,000 active export rows |
| Negative or null net | 0 | Current salary in each active export row |
| Null gross | 0 | Current salary in each active export row |
| Country/currency counts | 2,000 each in CA/CAD, DE/EUR, GB/GBP, IN/INR, US/USD | Live insight API |

**Production SQL is pending.** The active export cannot prove all salary-history rows, archived identities, foreign-key relationships, or Flyway history. Run the committed [read-only SQL](../scripts/verify-production.sql) and return its labelled rows. No local DB was substituted for the production DB in a hosted test.

### Two sample insight groups

| Job title / country / currency | Live API count | Average | Median | Min | Max | Production SQL match |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Software Engineer / IN / INR | 200 | 4050.50 | 3950.00 | 3000.00 | 25000.00 | Pending user SQL |
| Software Engineer / US / USD | 200 | 4050.50 | 3950.00 | 3000.00 | 25000.00 | Pending user SQL |

Both API samples match the same queries on the isolated local deterministic seed. That comparison is not a direct SQL verification of production.

## Read-only SQL on isolated local PostgreSQL

A fresh temporary PostgreSQL 16.15 database was migrated and seeded by the existing application, separate from the local demo and production. Every statement in `scripts/verify-production.sql` ran with ON_ERROR_STOP after setting default_transaction_read_only. Each SELECT returned exactly one labelled row.

| Check | Local result |
| --- | --- |
| active_employee_count | 10000 |
| duplicate_employee_numbers | 0 |
| duplicate_emails_case_insensitive | 0 |
| employees_with_unknown_country_or_department | 0 |
| salary_versions_negative_net_or_null_amounts | 0 |
| salary_versions_invalid_generated_totals_or_negative_inputs | 0 |
| employees_with_no_salary_version | 0 |
| active_counts_per_country_and_currency | [{"country_code": "CA", "currency_code": "CAD", "active_employees": 2000, "employees_with_current_salary": 2000}, {"country_code": "DE", "currency_code": "EUR", "active_employees": 2000, "employees_with_current_salary": 2000}, {"country_code": "GB", "currency_code": "GBP", "active_employees": 2000, "employees_with_current_salary": 2000}, {"country_code": "IN", "currency_code": "INR", "active_employees": 2000, "employees_with_current_salary": 2000}, {"country_code": "US", "currency_code": "USD", "active_employees": 2000, "employees_with_current_salary": 2000}] |
| current_salary_currency_mismatches | 0 |
| insight_Software_Engineer_IN_INR | {"median": 3950.00, "average": 4050.50, "country": "IN", "maximum": 25000.00, "minimum": 3000.00, "currency": "INR", "jobTitle": "Software Engineer", "employeeCount": 200} |
| insight_Software_Engineer_US_USD | {"median": 3950.00, "average": 4050.50, "country": "US", "maximum": 25000.00, "minimum": 3000.00, "currency": "USD", "jobTitle": "Software Engineer", "employeeCount": 200} |
| flyway_history | {"versions": [{"success": true, "version": "1", "description": "initial schema"}, {"success": true, "version": "2", "description": "use varchar for codes"}, {"success": true, "version": "3", "description": "salary templates"}], "failedMigrations": 0, "successfulVersionedMigrations": 3} |

Four existing Playwright API/data tests against this local PostgreSQL database passed (4 total, 0 failed, 0 skipped, 26.3 seconds): seed uniqueness/count, salary/reference rules, country aggregates versus direct SQL, and outlier groups. The last two exercise actual repository output and compare it with SQL. Local restart with seeding enabled retained 10,000 employees and 10,000 salary versions; the non-web seed-only command also exited successfully.

## Production safety

| Check | Result |
| --- | --- |
| POST /api/test/reset | 404, protected |
| GET /api/health | 200, UP |
| GET /actuator/health | 404: requested Actuator health availability is not implemented |
| /actuator, /actuator/env, /actuator/metrics, /actuator/beans, /actuator/mappings, /actuator/configprops | All 404 |
| Netlify CORS preflight | 200, exact Netlify Access-Control-Allow-Origin |
| Foreign-origin preflight and actual GET | 403, no Access-Control-Allow-Origin |
| Actual Render seed flag | Pending user confirmation of APP_SEED_ENABLED=false |
| Production second startup | Pending user action and before/after SQL; no production restart performed |

The code reads APP_SEED_ENABLED, not APP_SEED_ON_STARTUP. It has no explicit seed execution log. Neither an absent log line nor unchanged counts prove an idempotent seed runner did not execute. Check the flag and before/after results as described in [production-verification.md](production-verification.md). CORS tests sample one allowed and one disallowed origin; configuration inspection is still needed to prove no other origin is listed.

## Backend tests: fresh execution

Ran `mvn -B test` inside the backend unit-test image, rather than counting a cached Docker layer. **33 passed, 0 failed/errors/skipped across 14 classes.**

| Class | Tests | Passed | Failed/errors | Skipped |
| --- | ---: | ---: | ---: | ---: |
| HealthControllerTest | 1 | 1 | 0 | 0 |
| EmployeeSortTest | 3 | 3 | 0 | 0 |
| InsightDimensionTest | 1 | 1 | 0 | 0 |
| GlobalExceptionHandlerTest | 3 | 3 | 0 | 0 |
| InsightsRepositoryTest | 2 | 2 | 0 | 0 |
| SeedDataGeneratorTest | 2 | 2 | 0 | 0 |
| ActivityServiceImplTest | 2 | 2 | 0 | 0 |
| EmployeeExportServiceImplTest | 1 | 1 | 0 | 0 |
| EmployeeServiceImplTest | 2 | 2 | 0 | 0 |
| ExcelImportServiceImplTest | 5 | 5 | 0 | 0 |
| ReferenceServiceImplTest | 2 | 2 | 0 | 0 |
| SalaryCalculationServiceImplTest | 5 | 5 | 0 | 0 |
| SalaryServiceImplTest | 3 | 3 | 0 | 0 |
| UuidV5Test | 1 | 1 | 0 | 0 |

**Repository coverage limit:** InsightsRepositoryTest uses Mockito (2 tests), not a live PostgreSQL connection. No JUnit repository integration classes currently exist. The separately reported 4 local PostgreSQL API/data tests provide real DB query coverage; do not label the Mockito class as a PostgreSQL integration suite.

## Angular tests and production build

Fresh Docker frontend unit-test target: **17 passed, 0 failed, 8 spec files**, using Vitest and Angular TestBed. Production Angular build passed (499.92 kB initial bundle, below the configured warning/error budgets).

| Spec file | Passed |
| --- | ---: |
| shared/form-controls/form-controls.spec.ts | 4 |
| features/employees/employee-form-dialog.component.spec.ts | 2 |
| features/salary/salary-workspace-api.service.spec.ts | 2 |
| features/employees/employee-api.service.spec.ts | 2 |
| features/insights/insights-api.service.spec.ts | 1 |
| features/employees/salary-api.service.spec.ts | 1 |
| shared/currency-amount.pipe.spec.ts | 3 |
| shared/money.spec.ts | 2 |

## Secret scan

Gitleaks official container image scanned all 37 existing Git commits (~1 MB history) and the working tree (~49.12 MB). Both returned no leak findings. Generated dependencies/build outputs, Git internals in the directory scan, and scanner output files were excluded; application code, docs, and untracked artifacts remained in scope. Reports are redacted.

A supplemental credential-pattern scan of Git history identified 9 added-line occurrences of intentionally public local/test database credentials across six file/rule locations. These include the Compose/CI `salary` password and local PostgreSQL URLs in README, CI, `e2e/support/database.ts`, and `e2e/scripts/performance-check.mjs`. No unknown credential candidate or production password, token, or credential-bearing production JDBC URL was found. Backend YAML also contains public local defaults. These are findings to disclose, not production secrets. No scanner guarantees absence of every possible secret.

## Document review and remaining differences

- `docs/requirements.md`: 381 whitespace-delimited words, under the required 400-word one-page text budget; unchanged.
- The original assessment brief is unchanged.
- README now includes live links, cold-start warning, run/test and seed commands, decisions, limits, and verification links.
- Design now accurately describes the fixed demo actor, custom health endpoint, and coverage limits.
- Plan current status now records live hosting, local video, current test counts, hosted failures, and pending SQL/restart verification. Historical phase counts are explicitly historical.
- Original phase 2 planned POST /departments and /employees/new. Department creation is not implemented; reference lists are read-only and employee creation uses a dialog. This remains a recorded scope difference, not a completed feature.
- Allure dashboard/history and full tagged HR journeys remain deferred SHOULD work.
- Earlier PDF documents describe application commit 1dc488e and are historical snapshots, not evidence that these final checks passed.

## Commit history at application baseline

The 37 commits show requirements/design before feature work, incremental feature/refactor/test/CI commits, UI refinements, and deployment configuration changes. Some commits are styling/documentation-only rather than complete vertical slices. The final review adds a separate documentation/verification-script commit.

```text
2f747d3 chore: point production frontend at deployed API
3a5ecfc docs: prepare assessment documents and demo handover
5b4bbca fix: correct dockerignore for Render build
1dc488e feat: share text fields and styled dropdowns across all screens
b5f11d4 style: use simple equal-height template filter controls
29135ef style: align form labels and template filters
4392123 style: refine shared form and button components
f0f641a docs: record responsive UI verification
11db78b feat: improve responsive navigation and page guidance
ceaf048 docs: add AI review log and trade-offs
8e68f73 docs: record template-first salary workflow and gates
634c958 feat: add reusable salary templates and direct assignment
7a621cf feat: redesign responsive workspace with light navigation and badges
ae59781 feat: sort employees in PostgreSQL and export the current view
e773e93 feat: add employee modal editor with shared dialog and discard guard
d66035d refactor: isolate backend queries and enforce service boundaries
30455db chore: align local CI and Render to PostgreSQL 16
eba8c7e refactor: clarify backend layers and standardize API contract
5c611f5 chore: trim Docker build contexts
c1ce6ff fix: align persistence code types and CORS origins
834eef9 docs: add assessment demo and submission checklist
1fe3ada ci: run tests and publish review reports
e3ceb2d chore: configure Render and Netlify auto deploy
84fcf2c docs: update assessment execution record
8d547ab fix: expose activity screen in navigation
bd05935 feat: add read-only activity feed
2ba0117 feat: add spreadsheet import and filtered export
ea3f150 feat: add currency-aware pay insights
bd9f462 feat: seed 10000 deterministic employee records
42bfd62 feat: add effective-dated salary history
326edfd feat: add employee directory and audit trail
d11a706 chore: add postgres and flyway foundation
1837b00 docs: align project notes with approved design
3aedc79 docs: confirm design decisions
ea55907 docs: requirements and architecture design
5215180 docs: capture salary app architecture draft
877f0f6 docs: define salary management requirements
```

## Remaining user actions

1. Run scripts/verify-production.sql against Render PostgreSQL and paste only its labelled results.
2. Confirm APP_SEED_ENABLED=false in Render, then perform the documented controlled restart and before/after SQL check.
3. Confirm FRONTEND_ORIGIN has only the intended Netlify origin.
4. Decide whether the known hosted-test and Actuator-path gaps must be fixed in a separately authorized code/test change before submission.

Evidence files remain in `tmp/final-review` and `e2e/reports`; generated browser reports and videos are separate artifacts, not Git content.
