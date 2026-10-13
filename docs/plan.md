# Salary Management Assessment: Phased Delivery Plan

**Status:** Execution tracking as of 2026-10-14. Phases 0–6 are implemented and committed. Phase 7 is in progress. Public hosting, the hosted smoke test, and the demo recording still need account access and a running local browser environment.

## Summary

The original delivery estimate was **24 focused hours**, targeting three days at eight hours per day. The user requested completion today, so MUST work takes priority and account-dependent deployment steps are prepared for immediate provisioning. The estimate is a guide, not a claim that deployment or recording is complete.

The stack is Java 17, Spring Boot, Spring Data JPA, Flyway, PostgreSQL, standalone Angular with Angular Material and strict TypeScript, Playwright with TypeScript, and Docker Compose. Keep salary data synthetic. Use one commit per tested vertical slice. Render and Netlify configurations use provider GitHub auto-deploy; GitHub Actions runs tests only.

Confirmed defaults: required employee number, name, work email, hire date, country, department, and job title; US/USD, India/INR, UK/GBP, Germany/EUR, Canada/CAD; gross = basic + allowances; net = gross - deductions; nonnegative inputs with two decimal places and HALF_UP; append-only effective-dated salary snapshots; separate insight groups by currency; IQR outliers grouped by job title + country + currency with at least five salaries; stateless atomic create-only Excel imports; soft archive; and a small deterministic local/test reset dataset.

## Phase schedule

| Phase | Estimate (hours) | Dependency | Completion |
|---|---:|---|---|
| 0. Docs and scaffold | 0.5 | Existing repo | Done: `1837b00`, plus approved design commits |
| 1. Foundation and thin-slice hosting | 3.5 | Phase 0 | Code done: `d11a706`; hosting configured, provider deployment not yet run |
| 2. People and departments | 4 | Phase 1 | Done: `326edfd` |
| 3. Salary and history | 3 | Phase 2 | Done: `42bfd62` |
| 4. Deterministic 10,000-person seed | 2 | Phase 3 | Done: `bd9f462` |
| 5. Pay insights | 2.5 | Phase 4 | Done: `ea3f150` |
| 6. Excel plus activity | 3.5 | Phases 2 and 3 | Done: `2ba0117`, `bd05935`, navigation fix `8d547ab` |
| 7. Tests, CI, performance | 2 | Phases 2–6 | In progress; local Docker unavailable; report and CI configuration being completed |
| 8. Public deployment, demo, submission | 2 | All MUST work | Pending provider login, repository reviewer identity, and browser recording access |
| **Total** | **24** |  |  |

Day 1 is phases 0–2 (8 h). Day 2 is phases 3–5 plus a 0.5 h schedule buffer (8 h). Day 3 is phases 6–8 (7.5 h) plus a 0.5 h schedule buffer. Phase 1 deploys the thin slice; phase 8 verifies and refreshes the final deployment.

## Hosting recommendation

For the three-day assessment, the best no-cost starting choice is **Render Free for the Spring Boot API and PostgreSQL, plus Netlify Free for the static Angular app**. Both let us publish quickly without choosing a paid plan first. Netlify currently lists its Free plan at $0 with 300 monthly credits and a hard cap that pauses projects rather than charging over the limit. Render offers free web services, static sites, and PostgreSQL, but its free web service sleeps after 15 minutes without traffic and can take about a minute to wake. A free Render Postgres database has 1 GB storage, no backups, and expires 30 days after creation. Create it near the final demo and send the submission promptly; do not treat it as long-term hosting. See [Render free services and Postgres limits](https://render.com/docs/free) and [Netlify plans](https://www.netlify.com/pricing/).

Railway is the fallback if Render networking/build support or cold starts block the demo. Its free plan has $1 of monthly usage credit and a new-user trial gives a one-time $5 for up to 30 days; the Hobby plan is $5/month plus any usage over the included $5. Vercel Hobby is free for personal, non-commercial use, so Netlify is the safer fit for a public hiring-assessment submission. Recheck account-specific eligibility and limits before creating services. See [Railway plan limits](https://docs.railway.com/pricing/plans), [Railway free trial](https://docs.railway.com/pricing/free-trial), and [Vercel Hobby terms and limits](https://vercel.com/docs/plans/hobby).

## What to cut first if time runs short

Keep every MUST item: one-page requirements, working backend and UI for all four modules, 10,000-employee seed, core unit tests, a small set of Playwright checks, public deployment, demo video, incremental commits, and `/ai` plus design notes.

1. Cut the full `@e2e` cross-module journeys first. Keep a short `@sanity`, selected `@functional`, and essential `@data` checks.
2. Defer Allure history and dashboard; keep Playwright HTML, JUnit XML, and a plain-text result summary.
3. Defer advanced charts and visual polish; keep readable tables and labeled summary cards.
4. Defer optional filters and configurable import modes. Keep the agreed create-only, atomic import and filtered export.
5. Defer extra performance tuning after measuring the seeded dataset; keep database pagination, required indexes, and one recorded query check.

Do not cut any of the four modules, the seed, core unit tests, the public demo, or the video. Do not use real employee or salary data.

## Shared implementation contracts

- API responses use DTOs. JPA entities stay inside the backend. Monetary values use `BigDecimal` and PostgreSQL `NUMERIC`, never floating point.
- Employee list and export filtering happen in PostgreSQL. Employee pagination is server-side and bounded.
- Countries are US/USD, IN/INR, GB/GBP, DE/EUR, and CA/CAD. Do not convert currencies or compare values across currencies.
- The import workbook columns are: employee number, first name, last name, work email, hire date, country code, department, job title, currency, basic, allowances, deductions, and salary effective date.
- Archived employees retain salary history but are excluded from normal directory results, exports, and insights. Duplicate employee numbers and case-insensitive email addresses are rejected.
- Tables are `countries`, `departments`, `employees`, `salary_versions`, and `activity_log`. There are no salary component, import batch, or import error tables. Flyway migrations own the schema.
- Proposed screen routes: `/employees`, `/employees/:id`, `/insights`, and `/import-export`. Add stable `data-testid` attributes as each screen is built.
- Keep Playwright code in `/e2e`: `tests/`, `pages/`, fixed `fixtures/`, reset/API/report `support/`, and `playwright.config.ts`. Use `BASE_URL` and `API_URL` environment variables with local Compose defaults.
- REST paths in the approved `docs/design.md` are the implementation contracts. Record any needed change in `docs/decisions.md` before changing a feature contract.
- Each feature commit includes its relevant unit tests and Playwright tests. Add an `/ai` note with the prompt, any incorrect AI suggestion, and the change made.

## Phase 0. Align docs and clean the scaffold

**Estimate:** 0.5 h. **Depends on:** existing repo. This phase aligned the source documents, stack, package structure, and initial scaffold before application features were built.

### Tasks

1. **[MUST | docs] Correct the project record.** Update README, AGENTS, requirements, design, architecture, decisions, and commit plan to match the original assessment brief and final stack. Keep `docs/assessment-brief.md` verbatim. Include hire date, accepted data/calculation/import defaults, Render/Netlify, and the phased vertical-slice workflow. **User sees:** one consistent source of truth in Markdown. **API/DB/screen:** none; these docs define the contracts. **Edge cases:** keep original assessment wording unchanged; preserve deliberately excluded payroll/tax/payslips/approvals/login; don't silently convert proposals into facts beyond the accepted defaults.
2. **[SHOULD | setup] Update `.gitignore`.** Ignore Node/Maven output, local env/secrets, Playwright test output, traces/videos, Allure results, and generated reports while allowing checked-in examples such as `.env.example`. **User sees:** generated files and credentials stay out of normal Git status. **API/DB/screen:** none. **Edge cases:** do not ignore source test fixtures, the seed script, required docs, or the report artifact selected for submission.
3. **[MUST | setup] Audit and clean the exploratory scaffold.** Align the Angular shell and dependencies; use the approved PostgreSQL/Flyway setup, Docker Compose, and test-only reset guard. Preserve the assessment source DOCX and other user documents. **User sees:** a clean Java/Angular starting point. **API/DB/screen:** no user-facing behavior yet. **Edge cases:** inspect untracked files before deleting; keep user-created documents and any work that cannot be safely regenerated.

### Tests added

- **Unit:** none; this phase changes project records and setup only.
- **Playwright:** none.

### Definition of done

All tracked project docs describe Java 17/Spring/PostgreSQL and Angular/Material. The original brief is unchanged. Generated files/secrets are ignored. No H2 database or React instructions remain in the application setup.

### Planned commit messages

- `docs: align assessment requirements and architecture`
- `chore: clean scaffold and ignore generated files`

### Risks and decisions needed

The initial Angular shell may be incomplete or generated for an unsupported Angular release. Reuse it only after aligning to the current stable Angular + Material pair; replace generated files if necessary, without expanding product scope.

## Phase 1. Foundation and deployed thin slice

**Estimate:** 3.5 h. **Depends on:** Phase 0. Deploy the first small but real vertical slice before building the remaining modules.

### Tasks

1. **[MUST | setup] Establish Spring, PostgreSQL, and Flyway locally.** Configure Java 17, Spring Boot, Spring Data JPA, validation, PostgreSQL driver, Flyway, Docker Compose, environment variables, and health reporting. Create the first migration for countries, departments, and employees. **User sees:** `docker compose up` starts a healthy API and database. **API:** `GET /actuator/health`. **Tables:** `countries`, `departments`, `employees`. **Screen:** Angular app shell with navigation and an empty employee list. **Edge cases:** database unavailable, migration failure, missing/invalid environment variables, timezone-safe `hire_date`, and public health output that does not expose credentials.
2. **[MUST | feature] Ship the first directory slice.** Add a paged `GET /api/employees` response and render its empty state in the Angular Material directory screen. **User sees:** a working Employees page and clear empty/loading/error states. **API:** `GET /api/employees?page=0&size=20`. **Tables:** the Phase 1 three tables. **Screen:** `/employees`. **Edge cases:** empty result, invalid page/size, unavailable API, and no unbounded query.
3. **[MUST | test] Add the reusable Playwright base and sanity check.** Set base URL/API URL from environment variables with local Compose defaults, use page objects, and create `@sanity` health/home/directory checks. **User sees:** one command can verify the deployed thin slice. **API:** `/actuator/health`, `GET /api/employees`. **Tables/screens:** read-only checks on employee tables and `/employees`. **Edge cases:** app/API unavailable must fail with a clear reason; no fixed sleeps; honor environment URLs.
4. **[MUST | deployment] Deploy the thin slice.** Deploy Render Postgres and Spring API, publish Angular static files to Netlify, set CORS and environment variables, and verify the public URLs. **User sees:** public home and employee-list routes, even before the full feature set is complete. **API/DB:** health and empty employee list using Render Postgres. **Screen:** `/employees`. **Edge cases:** API cold start, CORS, build path, `/actuator/health` exposure, and no secret checked into Git.

### Tests added

- **Unit:** Spring context/health and repository mapping smoke tests; Angular app-shell rendering test.
- **Playwright:** `@sanity` health, home page, empty employee list, and insights route placeholder loads without a console/page error. Keep this suite under one minute.

### Definition of done

Local Compose starts PostgreSQL and the API; Flyway runs on an empty DB; the Angular screen can load the API; the public URL serves the same thin slice. The sanity suite passes against local and hosted environment variables.

### Planned commit messages

- `chore: add Spring Angular and PostgreSQL foundation`
- `feat: deploy the first employee directory slice`
- `test: add Playwright sanity suite`

### Risks and decisions needed

This is the earliest deploy and should use the Render DB only shortly before submission due to its 30-day expiry. Hosting access, service region, environment variables, and cross-origin configuration can block the estimate. Use synthetic data only.

## Phase 2. People and departments

**Estimate:** 4 h. **Depends on:** Phase 1.

### Tasks

1. **[MUST | feature] Complete employee and department maintenance.** Add country and department reference endpoints and employee detail/create/update/archive endpoints. Normalize email before uniqueness checks. **User sees:** HR can create an employee, open details, edit fields, choose a country/department, and archive an employee. **API:** `GET /api/countries`, `GET/POST /api/departments`, `GET/POST /api/employees`, `GET/PUT/DELETE /api/employees/{id}`. **Tables:** `countries`, `departments`, `employees`; archive uses `archived_at` or `active` status. **Screens:** `/employees`, `/employees/new`, `/employees/:id`, employee form and department selector. **Edge cases:** required hire date, malformed/future hire date policy, duplicate employee number, case-insensitive duplicate email, unknown country/department, blank names, overlong fields, missing IDs, archived record updates, and validation error format.
2. **[MUST | feature] Add search, filters, and server-side pagination.** Apply query, country, department, job title, and active/archive criteria in the shared repository specification. **User sees:** responsive directory controls and stable page counts. **API:** `GET /api/employees?q=&country=&department=&jobTitle=&page=&size=`. **Tables:** `employees`, joined `countries`, `departments`. **Screen:** `/employees`. **Edge cases:** query escaping, invalid department UUID, page size over the limit, no matches, filters with page past end, and archived employees excluded by default.
3. **[MUST | test] Add people tests with the slice.** **User sees:** validated changes stay correct as features evolve. **API/DB/screens:** test the endpoints, constraints, and list/detail/form states above. **Edge cases:** cover both server validation and database uniqueness, paging boundaries, archive preservation, and stale IDs.

### Tests added

- **Unit:** JUnit 5/Mockito tests for employee validation, email normalization, department/country lookup, filter bounds, and archive behavior; Angular form/API-service tests with deterministic fixtures.
- **Playwright:** `@functional` create/edit/archive, search, filters, and pagination; keep one short `@sanity` list assertion.

### Definition of done

HR can add, find, view, edit, and archive a person; department/country values are controlled and validated. List endpoints stay paginated in SQL, and unit plus feature-level Playwright tests are included in the same commit.

### Planned commit messages

- `feat: add employee and department maintenance`
- `feat: add employee search filters and pagination`

### Risks and decisions needed

Department maintenance is in the selected scope but the brief does not specify which fields departments have. Default to unique department name and active status. Confirm any country/department list display needs during implementation, not by adding unrelated HR fields.

## Phase 3. Salary structure and history

**Estimate:** 3 h. **Depends on:** Phase 2.

### Tasks

1. **[MUST | feature] Add salary amounts and immutable history.** Store effective-dated snapshots in one row with basic, allowances, deductions, and generated gross/net. **User sees:** employee detail shows current amounts, currency, effective date, and prior versions. **API:** `GET /api/employees/{id}/salary-history`, `POST /api/employees/{id}/salary-history`. **Tables:** `salary_versions` linked to `employees`. **Screen:** `/employees/:id` Salary structure with edit form and history list. **Edge cases:** missing employee, zero values, negative inputs, deductions above gross, duplicate/backward effective date, exact two-decimal rounding, unknown currency, and transactional rollback if any amount is invalid.
2. **[MUST | test] Verify salary calculations and change history.** **User sees:** totals and past values remain explainable after edits. **API/DB/screens:** test create/current/history contract and display. **Edge cases:** BigDecimal precision, HALF_UP boundary, zero allowances/deductions, no negative net, dates at boundary, and no mutation of old snapshots.

### Tests added

- **Unit:** JUnit 5/Mockito tests for `gross = basic + allowances`, `net = gross - deductions`, rounding, component validation, effective-date rules, and append-only history; Angular salary form and formatted totals tests.
- **Playwright:** `@functional` change salary structure, verify gross/net, then verify the previous version remains in history.

### Definition of done

Salary amounts use decimal database types and `BigDecimal`. Updating salary creates a new dated snapshot. The detail screen shows computed totals and history. Invalid amounts cannot persist.

### Planned commit messages

- `feat: add effective-dated salary structures and history`

### Risks and decisions needed

Only the accepted formula and scale are assumed. Country-specific pay frequency and tax are intentionally out of scope. Do not imply that this feature runs payroll.

## Phase 4. Deterministic 10,000-employee seed

**Estimate:** 2 h. **Depends on:** Phase 3.

### Tasks

1. **[MUST | feature] Add repeatable seed command.** Create deterministic fictional countries, departments, employees, hire dates, and salary snapshots; use prepared/batched inserts and a documented command. **User sees:** one command prepares a full 10,000-person assessment dataset. **API:** no public seed endpoint; application startup only when the seed property is enabled. **Tables:** countries, departments, employees, salary versions. **Screens:** seeded rows appear in `/employees`, `/insights`, and `/employees/:id`. **Edge cases:** repeated invocation, partial failure, deterministic UUID v5 IDs/emails, referential order, all 50 role/country/currency groups count 200, and no personal or real salary data.
2. **[MUST | test] Add seed integrity checks.** **User sees:** a repeatable proof that the required dataset is valid. **API/DB/screens:** query PostgreSQL directly; verify representative employee detail. **Edge cases:** exactly 10,000 active seeded employees, no duplicate employee numbers/emails, no null/negative components, valid country/department links, and each gross/net formula.

### Tests added

- **Unit:** deterministic generator tests for repeatability, valid values, and correct rounding.
- **Playwright:** `@data` uses API request client to trigger/inspect the seed, asserts exactly 10,000 employees and sample records; direct DB checks run from a separate test helper, never through the browser.

### Definition of done

The documented seed command produces exactly 10,000 deterministic fictional employees with valid salary data and relationships. Running it twice has documented, safe behavior and leaves the required count unchanged.

### Planned commit messages

- `feat: seed 10000 deterministic employee records`

### Risks and decisions needed

Do not implement the seed as 10,000 HTTP round trips. Choose deterministic batch inserts. Keep seed generation separate from the 12-person fixed Playwright fixture dataset.

## Phase 5. Pay insights

**Estimate:** 2.5 h. **Depends on:** Phase 4.

### Tasks

1. **[MUST | feature] Add currency-aware pay summaries.** Aggregate generated gross pay in PostgreSQL by country, department, and job title, always grouping by currency. **User sees:** average, median, minimum, maximum/range, sample size, and currency for selected dimensions. **API:** `GET /api/insights/pay?groupBy=country|department|jobTitle&country=&department=&jobTitle=`. **Tables:** employees, salary versions, countries, departments. **Screen:** `/insights` with filters, summary cards, and accessible tables. **Edge cases:** empty groups, archived employees excluded, missing salary, small groups, precision/rounding of displayed aggregates, invalid grouping, and currencies never combined.
2. **[MUST | feature] Add within-role outlier results.** Use IQR, grouped by job title + country + currency, with a five-employee minimum. **User sees:** employees outside those group bounds, with the rule explained. **API:** `GET /api/insights/outliers?country=&department=&jobTitle=`. **Tables:** employees and salary versions. **Screen:** `/insights` outlier section. **Edge cases:** fewer than five comparable employees, identical salaries, exact quartile boundary, missing values, and no cross-currency comparison.
3. **[MUST | test] Compare insight results to direct SQL.** **User sees:** aggregates agree with stored seeded salary data. **API/DB/screens:** query the endpoint and independent PostgreSQL query. **Edge cases:** currency groups, empty groups, archived rows, median with odd/even counts, and outlier minimum sample size.

### Tests added

- **Unit:** JUnit tests for query filters, group currency boundaries, median (odd/even), min/max, and IQR cases; Angular insights loading/empty/error and labels tests.
- **Playwright:** `@functional` verify displayed insight numbers change after a salary edit; `@data` compare selected summary totals against direct database queries.

### Definition of done

The insight screen supports each agreed grouping, labels currency, excludes archived employees, and matches database calculations. Outliers follow documented IQR boundaries and minimum sample size.

### Planned commit messages

- `feat: add currency-aware pay insights and outliers`

### Risks and decisions needed

PostgreSQL median support and query cost need measurement. The implementation must avoid loading all salary rows into Java memory. Explain the IQR method in the screen or a help note.

## Phase 6. Excel import and export

**Estimate:** 3.5 h. **Depends on:** Phases 2 and 3.

This phase also includes the approved activity-log vertical slice: append audit events with each successful employee, salary, import, and export mutation; expose a read-only server-paged feed.

### Tasks

1. **[MUST | feature] Provide the workbook template and preview.** Download the agreed template; upload `.xlsx`; parse and validate without persisting employee changes. **User sees:** template download and a preview with valid rows and field-level errors by row. **API:** `GET /api/imports/template`, `POST /api/imports/preview`. **Tables:** existing employees, countries, and departments are read for validation; preview state remains in the client only. **Screen:** `/import-export` template/upload/preview steps. **Edge cases:** missing/duplicate headers, empty workbook, wrong type, oversized file/row count, malformed workbook, invalid date/currency/amount, duplicate identifiers within file, conflict with existing employee/email, and spreadsheet formula cells.
2. **[MUST | feature] Confirm valid create-only imports atomically.** Permit confirmation only when the whole uploaded workbook is valid; revalidate uniqueness and references in the write transaction. **User sees:** confirmation count, success result, and no partial import on error. **API:** `POST /api/imports/confirm`; the workbook is re-uploaded and no batch ID is stored. **Tables:** `employees`, `salary_versions`, `activity_log`. **Screen:** preview confirmation and result. **Edge cases:** stale preview, repeated submission, database uniqueness race, transaction rollback, employee missing required hire date, and salary formulas that produce negative net.
3. **[MUST | feature] Export the current filtered employee view.** Stream the selected columns into an `.xlsx` file using the same filters and current salary data. **User sees:** a download whose rows match the list filters. **API:** `GET /api/employees/export?query=&country=&department=&jobTitle=`. **Tables:** same employee and salary joins. **Screen:** `/import-export` export controls plus directory filter state. **Edge cases:** zero rows, large result, archived rows excluded, currency included, formula-safe text cells, and filters URL-encoded consistently.
4. **[MUST | test] Add workbook-specific tests.** **User sees:** bad rows are explained and imports do not partially write data. **API/DB/screens:** test preview/confirm/download end to end. **Edge cases:** valid template, invalid and mixed files, duplicate rows, empty file, unsafe formula text, and exact imported row count.

### Tests added

- **Unit:** JUnit/Mockito tests for header mapping, parsing, field validation, error aggregation, atomic confirmation, export filter mapping, and spreadsheet formula escaping; Angular preview and row-error rendering tests.
- **Playwright:** `@functional` template download, valid import, bad-file row error report, filtered export contents, and `@data` exact confirmed row count.

### Definition of done

The HR user can download the template, preview row-level validation, correct errors before confirming, atomically create a valid batch, and export the filtered view. Invalid previews make no employee/salary writes.

### Planned commit messages

- `feat: add spreadsheet template and import preview`
- `feat: confirm atomic imports and export filtered employees`

### Risks and decisions needed

File parsing can consume memory or accept formula payloads. Enforce file and row limits, parse safely, and do not evaluate formulas. The accepted create-only behavior deliberately rejects updates to existing employee records.

## Phase 7. Test coverage, CI, and performance check

**Estimate:** 2 h. **Depends on:** Phases 2–6.

### Tasks

1. **[MUST | test] Complete the smallest useful Playwright suite set.** Provide reusable page objects for employee list, detail, insights, import/export, and activity; a deterministic reset with six fixed employees; environment-driven base/API URLs; and `/e2e` npm scripts `test:sanity`, `test:functional`, `test:data`, `test:e2e`, `test:all`, and `report:open`. **User sees:** independent tests that can be run locally without the 10,000-row seed except for `@data`. **API:** `POST /api/test/reset` is enabled only by the local Compose property and is disabled by default. **Tables:** fixed fixture rows in employees, salary versions, and audit log. **Screens:** all module routes. **Edge cases:** tests never depend on order, no random input, no fixed sleeps, reset cannot be enabled in production, and each test begins from known state.
2. **[MUST | test] Add GitHub Actions and concise reports.** Run backend/frontend unit tests and tagged Playwright `@sanity`, selected `@functional`, and required `@data`; upload Playwright HTML and JUnit XML as artifacts. Print total/passed/failed/skipped/duration and failed test names with one-line reason. Capture screenshots on failure and video/trace on first retry. Add a script that writes one detailed HTML report under `/e2e/reports`. **User sees:** every push produces reviewable status and downloadable reports. **API/DB/screens:** CI starts PostgreSQL and app services, checks all routes. **Edge cases:** preserve test output even on failure; never upload credentials or real data; constrain seed job to the test service.
3. **[SHOULD | test] Add Allure history and full journeys.** Add Allure history dashboard and one cross-module employee import-to-salary-to-insights-to-export `@e2e` journey. **User sees:** trend, tag breakdown, and failure context through `report:open`. **API/DB/screens:** reuse all four modules. **Edge cases:** preserve `history` between CI runs, video/trace on first retry, screenshots on failure, keep report generation separate from test assertions.
4. **[MUST | performance] Check seeded database query behavior.** Run representative 10,000-row directory and insight queries; add measured indexes only. **User sees:** stable list filtering and usable insights at target dataset size. **API:** employee search/list/export and pay insights. **Tables:** employees, salary versions, country/department references. **Screens:** directory, insights, export. **Edge cases:** avoid N+1 loading, enforce bounded pages, stream large exports, and keep aggregate computation in PostgreSQL. Save `EXPLAIN ANALYZE` plans in `e2e/reports/performance-summary.txt`.

### Tests added

- **Unit:** add missing high-value boundary tests; do not pad coverage with trivial tests.
- **Playwright:** `@sanity`, a few `@functional`, essential `@data`; `@e2e` and full Allure are SHOULD. Each suite can run alone through stable npm scripts.

### Definition of done

Required workflows pass locally and in GitHub Actions. CI artifacts include an HTML report and JUnit XML. Tests are tagged, independent, deterministic, use page objects/test IDs, and run without fixed sleeps. The report includes clear failures. Core SQL paths have measured results and justified indexes.

### Planned commit messages

- `test: add deterministic Playwright functional and data suites`
- `ci: run test suites and upload reports on every push`
- `perf: index measured employee and insights queries`
- Optional: `test: add Allure history and HR manager journey`

### Risks and decisions needed

The `@data` suite's direct database checks need test-only credentials available in CI. Keep credentials scoped to CI secrets. If runtime is too long, run the seed/invariant checks once per data job, not before every functional test. The optional Allure history path may be fragile until there are several runs.

## Phase 8. Final deployment, demo, and submission

**Estimate:** 2 h. **Depends on:** Phases 1–7.

### Tasks

1. **[MUST | deployment] Publish the complete public demo.** Provision the chosen Render/Netlify services, run the 10,000-person seed once, set production configuration and CORS, and verify the full workflows from the public app. **User sees:** a public URL with a working synthetic dataset and four modules. **API:** health, employee CRUD/search, salary, insights, import/export. **Tables:** all app tables migrated and seeded. **Screens:** employee list/detail, insights, import/export. **Edge cases:** API cold start, database free-tier expiry, stale deployment, migrations on a fresh DB, no real data, no reset route in production, and correct CORS/download behavior.
2. **[MUST | docs] Finish runbook and artifact notes.** Document setup, seed command, environment variable names, tests, deployment URL, known limits, architecture choices, prompts, AI corrections, and trade-offs. **User sees:** reviewers can run the app and understand decisions. **API/DB/screens:** document endpoint examples, schema, routes, and operating steps. **Edge cases:** no secrets, no unsupported claims, report/doc links resolve, and the exact assessment brief remains untouched.
3. **[MUST | deployment] Record the demo video.** Show the public app, search/edit and archive, salary update/history, insights, import preview/error correction, successful import, and filtered export. **User sees:** a short end-to-end assessment walkthrough. **API/DB/screens:** use public demo endpoints and the four main routes. **Edge cases:** use synthetic data, make cold start visible or warm the app immediately before recording, hide credentials, and keep video link/file accessible.
4. **[MUST | docs] Complete the final submission checklist.** Verify clean intended Git status, incremental history, repo privacy/access choice, public app URL, report artifact, demo video, and reply-ready repository link. **User sees:** a complete submission package and exact email-reply text for review. **API/DB/screens:** run health and smoke checks over public URLs. **Edge cases:** private repo link will not be reviewable unless reviewer access is granted; confirm repository sharing before emailing. The user replies to the assessment email; this plan does not send it.

### Tests added

- **Unit:** run the full backend and Angular unit suites; add no new tests unless final verification reveals a gap.
- **Playwright:** run `@sanity`, selected `@functional`, and required `@data` against the final configuration. Run `@e2e` only if time remains.

### Definition of done

The public app supports all four modules using synthetic data, the seed count is verified, public smoke checks pass, the demo video and artifacts are ready, the submission checklist is complete, and the repository link is ready for the user's email reply.

### Planned commit messages

- `docs: add deployment and assessment runbook`
- `chore: finalize public demo and submission artifacts`
- Keep the demo video and generated test report as submission artifacts unless the user asks to store them in Git.

### Risks and decisions needed

Public no-login access is acceptable only for entirely synthetic records. Confirm repository access for reviewers separately because the GitHub repo is private. Render Free cold starts can delay first access and its free Postgres expires after 30 days; keep the database newly created for submission or choose paid hosting if longer availability is required. Hosting changes must not exceed the user's approved spending.

## Submission checklist

- [ ] One-page requirements document written before feature work.
- [ ] Backend and Angular UI work for people/departments, salary structure/history, Excel import/export, and pay insights.
- [ ] Deterministic seed creates exactly 10,000 employees and passes data integrity checks.
- [ ] Core JUnit 5/Mockito tests and Angular unit tests pass.
- [ ] A small, deterministic Playwright suite passes; full Allure and journey suites are included if time permits.
- [ ] CI runs required suites on each push and retains reports.
- [ ] Public deployment is live with synthetic data only.
- [ ] Demo video is recorded and accessible.
- [ ] `/ai` notes, architecture, trade-offs, performance notes, and run instructions are committed.
- [ ] Git history shows small vertical-slice commits, with tests in the same feature commit.
- [ ] Reviewer access to the GitHub repository is confirmed.
- [ ] User replies to the original assessment email with the repository link.
