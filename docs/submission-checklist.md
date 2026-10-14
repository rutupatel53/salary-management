# Submission checklist

Updated: 2026-10-14

Post-template verification for commit `634c958`: Angular production build passes and all 13 frontend unit tests pass. Playwright discovers 22 tests, but the full suite has not been run after the template migration. Backend Maven tests and local database checks are pending: Docker Desktop cannot connect to its engine, and this environment cannot resolve Maven Central. Do not treat this final regression gate as complete until CI or a working local PostgreSQL/Maven setup verifies it.

## Ready in the repository

- [x] One-page requirements document is under 400 words.
- [x] Approved architecture, schema, API, UI flows, and trade-offs are documented.
- [x] Java/Spring/PostgreSQL backend and Angular UI cover people, salary history, Excel, pay insights, and activity.
- [x] The deterministic seed generator creates 10,000 fictional employees with 200 people in each of 50 outlier groups.
- [x] PostgreSQL 16.15 local database passes the 10,000-row data invariants and outlier checks.
- [x] Backend JUnit/Mockito suite passes: 28 tests, zero failures or errors.
- [x] Angular production build and unit suite pass: 11 tests, zero failures.
- [x] Playwright `@sanity`, selected `@functional`, and essential `@data` suites pass: 20 tests, zero failures.
- [x] PostgreSQL 16 performance plans and timings are recorded in `docs/performance.md` and the test report.
- [x] GitHub Actions runs tests only and uploads HTML/JUnit reports. Render and Netlify auto-deploy remain outside CI.
- [x] Render Blueprint, Netlify configuration, setup notes, AI notes, performance notes, and demo script are committed.
- [x] Incremental commits are pushed to the private [salary-management repository](https://github.com/rutupatel53/salary-management).

## Product UI readiness gates

- [x] Verify employee add/edit modals, field errors, discard confirmation, and focus restoration (9 functional tests; desktop/mobile modal screenshots inspected).
- [x] Add server-side sorting with clear table controls and regression tests. List and export share the same sort allowlist.
- [x] Deliver a dedicated salary-template workspace with country-specific Draft/Active/Archived status, component editing, copy, and validation.
- [x] Replace per-employee salary editing with searchable direct assignment, annual CTC entry, preview, confirmation, and immutable salary history. Keep a direct shortcut from employee list/detail.
- [ ] Review all screens for consistent navigation, shared controls, loading, empty, and error states.
- [x] Inspect the redesigned shell and main-screen layouts at phone/tablet/desktop widths (390/768/1440px); no page-level horizontal overflow.
- [ ] Inspect the salary-template and assignment screens at phone/tablet/desktop widths and confirm no page-level horizontal overflow.
- [ ] Run the final regression suite against the completed UI.

## Still required before emailing the submission

- [ ] Provision the Render Blueprint and confirm the public API health route and exact 10,000-person seed.
- [ ] Connect Netlify to the repository and confirm the public UI, `/api` proxy, import, and export.
- [ ] Confirm the latest pushed GitHub Actions run is green and download its report artifact.
- [ ] Record the public app walkthrough using [`demo-script.md`](demo-script.md).
- [ ] Add the assessment reviewer as a collaborator because the repository is private.
- [ ] Reply to the original assessment email with the repository and public app links, plus the demo video link.

## Environment verification limits

Local verification used Docker Compose with PostgreSQL 16.15. The performance timings are local warm-cache measurements and are not hosted performance guarantees. Public deployment and hosted smoke checks still require access to the user's Render and Netlify accounts. The video, reviewer access, and email reply also remain user actions.
