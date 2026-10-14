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

For each later review, failed test, or user correction that changes the work, add an entry with the phase, the prior report or claim, the finding, and the resulting change. Do not present unrun checks as passing.
