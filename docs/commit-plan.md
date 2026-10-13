# Incremental Commit Plan

The approved technical and product details live in [`design.md`](design.md). Keep each change small and reviewable. Every feature slice includes its migration, API, UI, unit tests, and matching Playwright tests.

## Planned order

1. `docs: confirm design decisions` — approved architecture and Codex package rules. Completed before implementation.
2. `docs: align project notes with approved design` — update older README, architecture navigation, requirements pointer, and decisions log.
3. `chore: clean scaffold and configure PostgreSQL` — remove H2 configuration, establish Spring/Angular dependencies, Flyway and Compose PostgreSQL, app health.
4. `feat: deploy first employee directory slice` — minimal working people list, public Render/Netlify integration auto-deploy.
5. `feat: add employee search and maintenance` — employee/department lookup, create/edit/archive, filters, paging.
6. `feat: add effective-dated salary history` — salary_versions with generated gross/net and validations.
7. `feat: seed 10000 deterministic employees` — UUID v5 employee IDs and balanced role groups.
8. `feat: add pay insights and role outliers` — SQL aggregates and job-title/country/currency IQR groups.
9. `feat: add stateless Excel import and filtered export` — preview/revalidate/atomic confirm, file export.
10. `feat: add activity log and read-only activity screen` — audit events for employee/salary/import/export actions.
11. `test: add Playwright sanity functional and data coverage` — deterministic fixtures, CI test workflow and report upload.
12. `perf: verify 10000-row queries and indexes` — add only measured indexes and record results.
13. `docs: add runbook and submission artifacts` — seed/test/deploy instructions, AI notes, performance notes, demo video and final checklist.

GitHub Actions runs tests only. Render and Netlify integrations deploy from GitHub directly. No deploy steps belong in CI workflows.
