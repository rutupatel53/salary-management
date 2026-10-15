# AI Review Log

This log records claims, review findings, and the corrections made. New entries are added when a review, failed test, or user correction changes the work.

## Entries

### 1. Database and seed verification

- **Phase:** Backend foundation and database setup.
- **Reported or claimed:** Migrations, generated salary columns, and the 10,000-employee seed had been verified.
- **What the review found:** PostgreSQL had never been run for that verification, so the migrations, generated columns, and seed were unverified.
- **What changed:** The verification claim is corrected here. These database items must be treated as unverified until PostgreSQL is started and the checks are run against it.

### 2. Backend test coverage across feature slices

- **Phase:** Backend employee, salary, and insight feature slices.
- **Reported or claimed:** Each slice was described as having its own meaningful unit test coverage.
- **What the review found:** The backend test count remained at nine across three feature slices, which did not demonstrate that tests had been added for each slice.
- **What changed:** The coverage claim is corrected here. Test counts and feature-specific coverage need to be checked before describing those slices as tested.

### 3. Playwright execution

- **Phase:** End-to-end test setup and feature work.
- **Reported or claimed:** Playwright tests were described as part of the tested feature slices.
- **What the review found:** The Playwright tests had been written but never executed.
- **What changed:** The execution claim is corrected here. A written test is not counted as passing until the suite has run and its result has been recorded.

## Ongoing review notes

### 4. Final deployed Playwright verification (15 October 2026)

- **Finding:** At application commit `2f747d3`, the live-target run executed 20 tests: 5 passed, 15 failed, none skipped. Three direct-SQL tests were excluded because production credentials are intentionally unavailable.
- **Cause:** Fourteen failures stop in fixture setup because they require POST `/api/test/reset`, which correctly returns 404 in production. One sanity check waits for the deleted `runtime-config.js`, so it times out even though the UI loads.
- **Outcome:** Recorded actual failures in `docs/final-verification.md`. No tests or feature code were changed, and production reset support was not enabled. Six supplementary read-only browser checks passed with no page errors. The hosted suite must not be described as green.

### 5. Database verification scope and repository coverage

- **Finding:** No production SQL credentials were used. The live API/export shows 10,000 active records and valid current net values, but this does not verify archived records, all salary history, or production Flyway rows.
- **Outcome:** Added the user-requested read-only `scripts/verify-production.sql`. It passed against an isolated local PostgreSQL 16.15 seed; four existing local API/data tests passed, comparing repository output with direct SQL. Production SQL remains pending until the user returns labelled results.
- **Coverage limit:** `InsightsRepositoryTest` has two Mockito tests; it does not itself connect to PostgreSQL. There is no JUnit repository integration suite. The separate Playwright/API/SQL checks provide actual PostgreSQL query coverage and must be named separately.

### 6. Production health and seed evidence

- **Finding:** `/api/health` returns 200; `/actuator/health` and six sampled Actuator paths return 404. There is no Actuator dependency, despite management properties in YAML. The requested health-only Actuator availability check therefore is not satisfied.
- **Finding:** The application reads `APP_SEED_ENABLED`, not `APP_SEED_ON_STARTUP`. Its seed runner has no dedicated execution log. Unchanged counts cannot prove that the idempotent runner did not execute.
- **Outcome:** Documented exact curl commands and flag/restart evidence needed. The production setting and restart are pending. A local restart with seeding enabled retained 10,000 employees and 10,000 salary versions. No production restart was performed.

### 7. Documentation and secret scan

- **Finding:** README lacked explicit live links, a standalone seed command, and detailed limits. Plan/checklist still contained old counts and hosting-pending claims. Design described the audit actor as configured even though it is fixed in code. The original plan also included department creation, which is not implemented.
- **Outcome:** Corrected current status and documented gaps without changing requirements or application code. Requirements remains 381 whitespace-delimited words, under 400. The original assessment brief is unchanged. Department/country references remain read-only; Allure history and full journeys remain deferred.
- **Security evidence:** Gitleaks found no leaks in 37 Git commits or the source/artifact working-tree scan. A supplemental credential-pattern review found only intentional public local/test credentials in CI, Compose, README, and database-test helpers. No production password, token, or credential-bearing production JDBC URL was found. Generated dependencies and build outputs were excluded; scans are evidence, not a guarantee of absence.

For each later review, failed test, or user correction that changes the work, add an entry with the phase, the prior report or claim, the finding, and the resulting change. Do not present unrun checks as passing.
