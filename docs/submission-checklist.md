# Submission checklist

Updated: 2026-10-14

## Ready in the repository

- [x] One-page requirements document is under 400 words.
- [x] Approved architecture, schema, API, UI flows, and trade-offs are documented.
- [x] Java/Spring/PostgreSQL backend and Angular UI cover people, salary history, Excel, pay insights, and activity.
- [x] The deterministic seed generator creates 10,000 fictional employees with 200 people in each of 50 outlier groups.
- [x] JUnit/Mockito test suite passes locally: 19 tests, zero failures or errors.
- [x] Playwright sanity, functional, and data tests are written and tagged. They have not run locally because Docker Engine is unavailable.
- [x] GitHub Actions test-only workflow and HTML/JUnit/performance report artifacts are committed. Provider deploys remain outside CI.
- [x] Render Blueprint, Netlify configuration, setup notes, AI notes, performance notes, and demo script are committed.
- [x] Incremental commits are pushed to `https://github.com/rutupatel53/salary-management`.

## Still required before emailing the submission

- [ ] Provision the Render Blueprint and confirm the public API health route and exact 10,000-person seed.
- [ ] Connect Netlify to the repository and confirm the public UI, `/api` proxy, import, and export.
- [ ] Confirm the pushed GitHub Actions run is green and download its report artifact.
- [ ] Record the public app walkthrough using [`demo-script.md`](demo-script.md).
- [ ] Add the assessment reviewer as a collaborator because the repository is private.
- [ ] Reply to the original assessment email with the repository and public app links, plus the demo video link.

## Environment verification limits

The backend unit tests and production Angular build have passed. The Angular CLI test builder does not discover spec files in this Windows workspace path containing spaces; the pushed workflow runs that suite on Linux. Browser suites and the seeded database have not run locally because the Docker service is unavailable. Do not describe deployment, a green CI run, or the video as complete until their checks above are done.
