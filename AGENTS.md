# Project rules for Codex

## Source of truth and working order

- Treat `docs/assessment-brief.md` as the verbatim source brief, `docs/requirements.md` as the product requirements, and `docs/design.md` as the approved architecture and decisions.
- The architecture is approved. Continue in MUST-first order from `docs/plan.md`. Do not wait between phases unless a step needs an account, permission, credential, or other action only the user can provide.
- Implement one vertical slice per commit. A slice includes its database change, API, screen, unit tests, and matching Playwright tests. Keep commits small and use the planned commit message where it still fits.
- After each phase, send a three-line status: `Done: ...`, `Tests passing: ...`, `Next: ...`.
- Update design/decision notes when an approved decision changes. Keep AI prompts, mistakes found, and corrections in `/ai`.
- Never put secrets or real employee/pay data in Git, test fixtures, logs, reports, or the public demo.

## Final stack

- Backend: Java 17, Spring Boot, Spring Data JPA, Flyway, PostgreSQL.
- Frontend: current stable Angular, standalone components, Angular Material, strict TypeScript.
- Tests: JUnit 5/Mockito and Angular's supported unit test runner; Playwright with TypeScript for end-to-end tests.
- Local environment: Docker Compose with PostgreSQL.
- Deployment: Render for API/PostgreSQL, Netlify for static Angular UI. Render and Netlify GitHub integrations auto-deploy; GitHub Actions runs tests only and must not deploy.
- Do not use React, Next.js, SQLite, or AngularJS 1.x.

## Backend package structure

Use a single Spring Boot application with layer-based packages under `...salary`:

```text
config/
constants/
controller/
dto/          common response envelope, request/response DTOs,
              shared filter and pagination DTOs
enums/
exception/    global exception handler and stable error response
mapper/       MapStruct mappers
model/        JPA entities
repository/   Spring Data repositories
service/      service interfaces and implementations
specification/shared database predicates for list and Excel export
validator/
util/
audit/
seed/
shared/       helpers reused across packages
```

Mirror production package paths in `backend/src/test/java`. Put reusable code in shared packages; never duplicate helpers inside services. Controllers only handle HTTP/DTO concerns. Services own business rules and transactions. Repositories perform database work. Do not expose JPA entities from APIs.

## Frontend structure

Under `frontend/src/app`:

```text
core/       app shell, API base service, HTTP interceptors, notification service
shared/     server-paged data table, filter bar, confirm dialog,
            empty/loading/error states, currency display, file upload box,
            pipes, validators
features/   employees, salary, insights, import-export, activity
```

Components never call `HttpClient` directly. Feature API services use the core API base service. Shared components expose stable `data-testid` attributes. Use standalone components and strict template/type checks.

## UI navigation

- Use a compact header with a hamburger that opens the left side menu as an overlay on desktop and mobile.
- The side menu starts closed and closes after route selection, Escape, or backdrop click.
- Feature screens include a short visible “How to use this page” guide with practical steps.
- Table actions that use icons include a tooltip and an accessible name; icon-only controls must remain keyboard reachable.
- Use the shared form and button tokens for control height, typography, borders, hover, focus, disabled, and responsive states.
- Keep form labels persistently above controls, use equal-grid spacing for multi-step guidance, and keep filter fields aligned on one desktop row before stacking on narrow screens.
- Keep the theme restrained: neutral surfaces, blue primary actions, and badges only for useful status or currency labels.

## Data and product rules

- Money is `BigDecimal` in Java and `NUMERIC(14,2)` in PostgreSQL. Never use binary floating point.
- `salary_versions.gross` and `.net` are PostgreSQL stored generated columns. `gross = basic_amount + allowances_amount`; `net = basic_amount + allowances_amount - deductions_amount` (equivalent to gross minus deductions). PostgreSQL generated expressions cannot reference another generated column, so net must use the base columns. Include a `CHECK (net >= 0)` constraint.
- Salary versions are append-only and effective-dated. New effective dates must follow the latest version. Currency is the employee country's configured currency and is stored on each version.
- All queries, exports, and insights use the stored generated gross/net values. Filter, sort, paginate, and aggregate in PostgreSQL. Bound page sizes; never load the complete employee list for UI or export.
- Outliers always group by job title + country + currency, use IQR, and require at least five salaries per group. The deterministic seed has 200 employees per each of the 50 such groups.
- Seed exactly 10,000 fictional employees using deterministic UUID v5 identifiers derived from employee numbers. The seed command is repeatable/idempotent. Keep it separate from the small fixed Playwright dataset.
- Excel import is stateless: preview writes nothing; confirm re-uploads and revalidates the file, then atomically creates all valid rows or none. Do not add import batch/row persistence tables.
- Archive employees; do not delete salary history or audit records. Activity log records employee/salary/import/export actions and has a read-only screen. Without login, use the configured `assessment-hr-manager` demo actor and label it unverified.
- Do not add payroll runs, tax, payslips, approval workflows, or role-based login.

## Playwright and CI

- Keep reusable Playwright setup in `/e2e`; use TypeScript, page objects for employee list/detail, insights, and import/export, `BASE_URL`/`API_URL` environment variables, and local Docker Compose defaults.
- Use independent deterministic tests, fixed fixtures, reset before each test, Playwright auto-waiting and web-first assertions. Never use random test data or fixed sleeps.
- Add stable test IDs with the screens and shared components. Include each feature's Playwright test in the same commit as the feature.
- MUST suites: `@sanity`, selected `@functional`, and essential `@data`. `@e2e` journeys and Allure dashboard are SHOULD, after MUST work is complete.
- GitHub Actions runs tests only: Java and Angular unit tests, Playwright suites, and report artifact upload. Render/Netlify deploy from their GitHub integrations, not CI.

## Engineering quality

- Validate inputs server-side and return a stable error envelope with field errors.
- JSON feature APIs use `ApiResponse<T>` with `status`, `success`, `message`, `data`, and `errors`. The envelope status must match the HTTP status. Use 201 for successful create/import operations, 400 for malformed or invalid input, 404 for missing records/routes, 409 for uniqueness or domain conflicts, and 500 with a generic message for unexpected errors. Binary downloads and `/api/health` keep their download/health response shapes.
- Keep employee search, country/department/job-title filters, pagination, identity conflict lookups, and insight aggregation in PostgreSQL. Do not fetch full employee sets and filter them in Java. Import parsing and export serialization may iterate bounded workbook rows/pages; avoid one database round trip per row.
- Use fixed, meaningful test data. Cover core salary calculations, constraints, history, filters, import behavior, currency grouping, and edge cases.
- Preserve database relationships and audit history. Build the smallest schema that meets requirements: templates store validated bounded component JSON; salary snapshots preserve the resolved breakdown; no import batch tables for stateless import.
- Measure queries with the 10,000-row seed before adding indexes. Record index and performance decisions in Markdown.
- Do not silently change requirements, formulas, schema, or API contracts. Update the design/decision record before implementing a material change.
