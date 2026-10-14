# AI work note: salary templates and direct assignment

Related commit: `634c958`

## Prompt and user direction

The user reviewed the first UI and asked for a responsive redesign, less sidebar emphasis, a calmer color system, visible status badges, and a procedure-based salary workflow. The reference screenshots showed a salary-template builder and a separate assignment flow. The user clarified that HR should not have to open every employee record to update salary. They approved annual CTC, fixed amounts and percentages of CTC/basic, one balancing allowance, employer contributions included in CTC, country-specific templates, immutable salary snapshots, and a concise preview-before-confirm flow.

## Design applied

- Added a separate salary-template workspace with search, status, country, copy, edit, and activate actions.
- Added salary assignments as a direct workspace. HR searches an employee, chooses a country-compatible active template, enters annual CTC and effective date, reviews annual/monthly calculations, then confirms.
- Kept a direct assignment shortcut in employee list/detail for HR who already found the person.
- Stored at most 20 validated template components in JSONB and copied resolved annual/monthly component amounts and template revision to each salary version.
- Kept basic, allowances, deductions, generated gross, and generated net in typed PostgreSQL columns for database constraints and insights.

## Review risks and corrections

- A per-employee-only editor adds unnecessary navigation for bulk HR work. The assignment workspace makes employee lookup the starting point and keeps profile navigation optional.
- Editing a template must not change old salary data. Salary versions therefore keep their own component breakdown and template name/revision snapshot.
- CTC and gross are different when employer contributions exist. The calculator treats CTC as gross earnings plus employer contributions, then calculates net as gross less employee deductions.
- Annual and monthly rounded amounts can differ by a cent after multiplication. The UI labels both periods and the calculator balances each period independently.
- Component validation must also run on the server. The template service validates component count, unique names, formulas, required basic, one balance allowance, country/currency, and stale revisions.

## Verification status

- Angular production build passed after the new screens were added.
- Backend calculation unit tests were added for template arithmetic and invalid cases.
- Frontend Vitest now uses a shared Angular test platform and one worker. The unit suite passes after fixing `TestBed` cleanup and resolving the employee dialog component template in its isolated tests.
- Backend Maven tests are still blocked: required dependencies are not cached, and this environment cannot resolve Maven Central. Docker Desktop also cannot connect to the PostgreSQL engine.
