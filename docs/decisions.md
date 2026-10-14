# Decision Log

Record confirmed choices here so the repo preserves decisions made in chat. The current source documents are [`requirements.md`](requirements.md) and [`design.md`](design.md). Update this log when implementation changes an approved choice.

## Confirmed decisions

| Date | Decision | Reason |
|---|---|---|
| 2026-10-14 | Build for the HR manager maintaining records for 10,000 fictional employees in five countries. | Directly addresses the assessment and avoids use of real salary data. |
| 2026-10-14 | Stack: Java 17, Spring Boot, Spring Data JPA, Flyway, PostgreSQL; standalone Angular, Angular Material, strict TypeScript; Playwright; Docker Compose. | User selected the stack; modern Angular is intended. |
| 2026-10-14 | Deploy API/PostgreSQL on Render and Angular static app on Netlify. Use provider GitHub auto-deploy; CI runs tests only. | Reduce deployment wiring and do not deploy from GitHub Actions. |
| 2026-10-14 | Scope is people/departments, salary/history, Excel import/export, pay insights, and read-only activity. Exclude payroll runs, tax, payslips, approvals, and role-based login. | Keep the assessment focused on salary management and pay questions. |
| 2026-10-14 | Employee fields include unique employee number, name, case-insensitive unique work email, hire date, country, department, and job title. Archive is soft delete. | Preserve employee identity and salary/audit history. |
| 2026-10-14 | Salary changes are append-only effective-dated snapshots. Inputs are nonnegative basic, allowances, and deductions; gross is basic + allowances; net is gross - deductions; use two decimals and reject negative net. New effective dates follow the latest. | Clear history and exact calculations without modeling payroll. |
| 2026-10-14 | HR defines reusable country/currency salary templates, then assigns one directly from a searchable workspace using annual CTC and effective date. CTC includes employer contributions; gross includes basic plus allowances; net subtracts employee deductions. Templates support fixed, percent-of-CTC, percent-of-basic, and one balancing allowance. | Avoid opening employees one by one and keep the assignment procedure clear. |
| 2026-10-14 | Salary template components are validated and bounded to 20 entries in JSONB. Each salary version stores template ID/name/revision and the resolved annual/monthly breakdown. Template revisions never alter existing snapshots. | Keep the schema small while preserving an auditable exact breakdown. |
| 2026-10-14 | Salary assignment requires an active template for the employee's country, the latest template revision, and an effective date after hire and after the latest salary. The server previews and revalidates before saving; employee and template rows are locked during assignment. | Prevent stale previews and concurrent changes from creating inconsistent history. |
| 2026-10-14 | PostgreSQL stores generated `gross` and `net` columns on `salary_versions`; insights/tests read these values. | Keep derived values consistent in storage. PostgreSQL net expression uses base columns because generated expressions cannot depend on another generated column. |
| 2026-10-14 | Currency is local to each of US/USD, India/INR, UK/GBP, Germany/EUR, Canada/CAD; never mix currencies. New salary versions follow the employee country currency. | Avoid misleading cross-currency comparisons. |
| 2026-10-14 | Outliers use IQR grouped only by job title + country + currency, minimum five salaries per group. | One consistent definition in all views. |
| 2026-10-14 | Seed exactly 10,000 fictional employees, distributed as 200 employees in each of 50 job-title/country/currency groups. Employee IDs use deterministic UUID v5 derived from employee number. | Every outlier group can be evaluated and repeated seed runs are stable. |
| 2026-10-14 | Excel import is stateless, create-only, revalidated at confirmation, and atomic. No import batch/row tables. | Avoid temporary batch persistence and partial imports. |
| 2026-10-14 | Add an activity log for employee, salary, import, and export actions. Activity screen is read-only. Without login use configured `assessment-hr-manager` actor, labeled unverified. | Record actions while preserving the no-login scope. |
| 2026-10-14 | Use one Spring Boot app with layer-based packages and the approved Angular `core/shared/features` layout. Components do not call `HttpClient`; shared components have stable test IDs. | Reuse shared code and keep screens maintainable. |
| 2026-10-14 | Implement small vertical slices; commit each with its unit and matching Playwright tests. After each phase report done/tests/next in three lines. | Keep progress reviewable and tests close to behavior. |
| 2026-10-14 | UI direction updated: use a hamburger in the compact top bar to open the left side menu as an overlay on both desktop and mobile. Start closed; close after navigation, Escape, or backdrop click. | The user clarified they want a side menu controlled by a responsive hamburger on web and mobile. |
| 2026-10-14 | Salary template row actions use icons with tooltips and accessible labels. Each feature screen provides a brief visible usage guide. | Reduce button text clutter while keeping the controls discoverable and the workflows clear. |
| 2026-10-14 | Standardize Angular Material form fields and buttons with shared dimensions, typography, borders, hover/focus feedback, and mobile wrapping. Keep template row action icons in one horizontal group. | Make controls feel consistent and usable across every feature instead of relying on unrelated local styles. |
| 2026-10-14 | Keep field labels persistently above controls; lay out page-guide steps as equal columns on desktop and stacked rows on mobile; align template filters in one grid row until the mobile breakpoint. | Remove placeholder-like labels and uneven spacing while preserving a compact, responsive workflow. |

## Implementation record

2026-10-15: All feature text fields and dropdowns now use shared Angular form components. `FieldControl` centralizes form state and validation; `TextFieldComponent` keeps the accepted 48px field style; `DropdownComponent` wraps Material Select with a consistent option panel. Reference lists use one pure mapping pipe. Pagination uses the same option-panel styling. Verification: production build, 17 frontend unit tests, and 14 Playwright functional tests pass; the mobile employee dialog has matching 48px controls and no horizontal overflow.

For each feature commit, add a row or note with its tests and any trade-off. Record prompts, AI errors, and corrections in `/ai`. Never record credentials or real employee data.
