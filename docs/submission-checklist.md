# Submission checklist

Updated: 2026-10-15

Final verification on 2026-10-15: the deployed frontend and API are reachable; live API/export count is 10,000 active employees. Fresh checks passed 33 backend tests, 17 Angular tests, the production build, four isolated local PostgreSQL data tests, and the read-only SQL script locally. Production Playwright is not green: 20 executed, 5 passed, 15 failed, none skipped; three production SQL tests were excluded pending user-run SQL results. Fourteen failures require the protected reset route and one sanity check expects a removed runtime-config file. Production SQL, the actual seed flag, and controlled restart evidence remain pending. See [the final report](final-verification.md); historical CI evidence below is not the current hosted result.

## Ready in the repository

- [x] One-page requirements document is under 400 words.
- [x] Approved architecture, schema, API, UI flows, and trade-offs are documented.
- [x] Java/Spring/PostgreSQL backend and Angular UI cover people, salary history, Excel, pay insights, and activity.
- [x] The deterministic seed generator creates 10,000 fictional employees with 200 people in each of 50 outlier groups.
- [x] PostgreSQL 16.15 local database passes the 10,000-row data invariants and outlier checks.
- [x] Backend JUnit/Mockito suite passes: 33 tests, zero failures or errors.
- [x] Angular production build and unit suite pass: 17 tests, zero failures.
- [x] Playwright `@sanity`, selected `@functional`, and essential `@data` suites pass: 23 tests, zero failures or skips.
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
- [x] Inspect the salary-template and assignment screens at phone/tablet/desktop widths; no page-level horizontal overflow or browser errors.
- [x] Required Playwright regression suite passes against the completed local UI (23/23 in CI).

## Submission artifacts prepared

- [x] Shareable product overview: [`submission-overview.md`](submission-overview.md) and the [two-page PDF](../output/pdf/Salary%20Management%20Assessment%20Overview.pdf).
- [x] Technical guide: [`technical-guide.md`](technical-guide.md) and the [nine-page PDF](../output/pdf/Salary%20Management%20Technical%20Guide.pdf).
- [x] Two-minute local demo with synthetic records, narration, chapter captions, and an optional English subtitle track.
- [x] [Demo transcript](demo-transcript.md) and [recording flow](demo-script.md).
- [x] [Reply draft](submission-email.md) prepared for the user to complete and send.
- [x] Verified [CI evidence](https://github.com/rutupatel53/salary-management/actions/runs/38616293398) is linked in the documents.

## Still required before emailing the submission

- [ ] Return the labelled production results from [`scripts/verify-production.sql`](../scripts/verify-production.sql).
- [ ] Confirm `APP_SEED_ENABLED=false` and complete the controlled restart checks in [`production-verification.md`](production-verification.md).
- [ ] Resolve or disclose current hosted test-harness failures and the `/actuator/health` path difference. Do not enable the reset route in production.

- [x] Public API health and active ACME seed count respond successfully; all-history SQL checks remain separate and pending.
- [x] Netlify routes, direct Render API calls, employee search, insights, and filtered export load. Write/import-confirm workflows remain unverified by hosted fixture-dependent tests.
- [ ] Confirm the final pushed GitHub Actions run is green and download its report artifact. The verified application run is already green.
- [ ] Upload the prepared demo file to an accessible sharing location. It is explicitly a local recording; optionally record the same flow on the public app.
- [ ] Add the assessment reviewer as a collaborator because the repository is private.
- [ ] Reply to the original assessment email with the repository and public app links, plus the demo video link.

## Environment verification limits

Local verification used isolated PostgreSQL 16.15. Performance timings are local warm-cache measurements, not hosted guarantees. The demo file exists locally at `output/demo/Salary Management Demo.mp4`; it lasts exactly 120 seconds and uses an offline synthetic voice. The video binary and generated test reports are submission artifacts rather than Git content. Production SQL and restart confirmation, hosted write-workflow verification, video sharing, reviewer access, and sending the email remain handover actions.
