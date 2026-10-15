# Salary Management Assessment

An HR salary-management web app for 10,000 fictional employees across five countries. The approved product scope and system design are in [`docs/requirements.md`](docs/requirements.md) and [`docs/design.md`](docs/design.md). The original assessment is preserved in [`docs/assessment-brief.md`](docs/assessment-brief.md).

## Live assessment

- Frontend: https://superlative-blancmange-ebe430.netlify.app
- API: https://salary-management-1vd3.onrender.com
- Health: https://salary-management-1vd3.onrender.com/api/health
- Final verification results, failures, and pending checks: [`docs/final-verification.md`](docs/final-verification.md).

The first request after inactivity can take about a minute while the Render free service wakes up. The public demo contains fictional data only. Credentials shown for local Compose and tests are public local-development defaults, not production credentials.

## Stack

- Java 17, Spring Boot, Spring Data JPA, Flyway, PostgreSQL
- Angular standalone, Angular Material, strict TypeScript
- Playwright with TypeScript for end-to-end tests
- PostgreSQL 16 for local, CI, and Render databases, with Docker Compose for local setup
- Render for API/PostgreSQL and Netlify for the Angular app; their GitHub integrations auto-deploy. GitHub Actions runs tests only.

## Product modules

1. Employee, department, and country directory
2. Country-specific salary templates, direct employee assignment by annual CTC, previewed monthly/yearly breakdowns, and append-only effective-dated history
3. Stateless Excel preview/validation, atomic create-only import, and filtered export
4. Currency-separated pay insights and role outliers
5. Read-only activity log

## Key decisions and trade-offs

- Use reusable, country-specific salary templates and assign them to employees with annual CTC. This avoids entering the same component rules for every person.
- Save each salary change as an effective-dated snapshot. Template edits do not rewrite an employee's past salary.
- Keep salary insights separate by currency. The app does not convert values or imply cross-country comparisons are equivalent.
- Make spreadsheet imports stateless and atomic. HR previews errors first; confirmation revalidates the file and creates all rows or none.
- Keep payroll runs, taxes, payslips, approvals, and role-based login out of scope. The product focuses on managing salary structures and answering HR pay questions.
- Use fictional seed data and a demo actor. This keeps the public assessment deployment safe without adding an authentication system.

## Start here

1. Read [`docs/requirements.md`](docs/requirements.md).
2. Read the approved architecture, schema, API, UI flows, and trade-offs in [`docs/design.md`](docs/design.md).
3. Follow [`AGENTS.md`](AGENTS.md) for source layout and engineering rules.
4. Keep decisions and build notes in [`docs/decisions.md`](docs/decisions.md) and [`ai/`](ai/README.md).
5. Follow [`docs/plan.md`](docs/plan.md) for the phase sequence.

## Local development

Use Docker Compose to run PostgreSQL, the API, and Angular UI:

```sh
docker compose up --build
```

Open `http://localhost:4200`; the API health route is `http://localhost:8080/api/health`. Flyway creates the schema and reference rows, then Compose loads the deterministic 10,000-person synthetic dataset. The smaller Playwright fixture set remains separate and its reset endpoint is available only in this local setup.

### Seed command

Compose enables the Java seed runner automatically. To run that same deterministic seed as a one-off local command:

```sh
docker compose run --rm -e APP_SEED_ENABLED=true salary-api --spring.main.web-application-type=none
```

The runner uses fixed UUID v5 IDs and conflict-safe inserts; it does not overwrite existing salaries. The application reads `APP_SEED_ENABLED`, not `APP_SEED_ON_STARTUP`. After the hosted seed is confirmed, set `APP_SEED_ENABLED=false` in Render. Keep `APP_TEST_TOOLS_ENABLED=false` in production. See the [production verification instructions](docs/production-verification.md) for read-only SQL and exact curl checks.

## Tests

Backend unit tests use JUnit 5 and Mockito. Angular unit tests use Vitest with Angular TestBed. Playwright runs from `/e2e`, with environment variables `BASE_URL` and `API_URL`. CI runs tests and saves reports; deploys are handled by Render and Netlify GitHub auto-deploy.

Install dependencies and run the suites from their project folders:

```sh
cd backend && mvn test
cd ../frontend && npm ci && npm test && npm run build
cd ../e2e && npm ci && npx playwright install chromium
npm run test:sanity
npm run test:functional
DB_URL=postgresql://salary:salary@127.0.0.1:5432/salary_management npm run test:data
npm run test:all
```

`test:all` prints counts and failed-test reasons, writes Playwright HTML and JUnit XML, and creates `e2e/reports/detailed-report.html`. `report:open` opens the full Playwright report. The local test reset endpoint is enabled only by Docker Compose; production keeps it disabled. Run `npm run perf:check` in `/e2e` after loading the seed to save PostgreSQL query plans.

If Java or Node is not installed locally, run the unit checks through the versioned container targets from the repository root:

```sh
docker build --target unit-tests -f backend/Dockerfile backend
docker build --target unit-tests -f frontend/Dockerfile frontend
```

## Deployment

Deployment is configured for GitHub auto-deploy. CI is test-only.

1. In Render, create a Blueprint from this repository and apply [`render.yaml`](render.yaml). It creates the `salary-management-api` Docker service and a PostgreSQL database. The service migrates the schema and loads only the deterministic synthetic seed. The test reset endpoint stays disabled.
2. In Netlify, import this repository as a site. The root [`netlify.toml`](netlify.toml) sets the `frontend` base directory, Node 22.22.3, build command, publish folder, and SPA fallback. Angular's production environment supplies the Render API URL to the core API service. The browser calls Render directly; set Render's `FRONTEND_ORIGIN` to the deployed Netlify origin (the synthetic demo Blueprint currently allows all origins). Subsequent GitHub pushes auto-deploy through Netlify.
3. Verify the deployed Render service's `/api/health`, then open the assigned Netlify site and confirm the employee and insights screens load. Check that the seeded employee filter reports exactly 10,000 rows. The production API URL is defined once in [`frontend/src/environments/environment.production.ts`](frontend/src/environments/environment.production.ts), selected by the production `fileReplacements` in `angular.json`. Development and Docker Compose keep the local `/api` proxy.
4. The repository is private. Add the assessment reviewer as a GitHub collaborator before sharing the repository URL. Do not add real employee or salary data.

Render's free PostgreSQL tier is temporary and the free API may sleep. Use the published app promptly for the assessment demo; check the current provider limits before provisioning.

## Known limits

- No login or authorization. Use synthetic records only; the audit actor is a fixed, unverified demo identity.
- Never combine currency totals. Insights group by currency; there is no exchange-rate conversion.
- Countries and departments are seeded reference lists. There is no department administration screen.
- Excel imports create only and accept at most 500 rows and 5 MB per file. Export reads database pages but returns the final workbook as a byte array.
- Render Free PostgreSQL expires 30 days after creation. Record the database's actual expiry date in Render and keep the assessment link available for reviewers. The free API can sleep, adding about a minute to the first request. [Provider limits](https://render.com/docs/free).
- The public health endpoint is `/api/health`. Actuator is not installed, so `/actuator/health` returns 404 too.
- Current functional fixtures require a test-only reset route. They run in local/CI environments; they cannot pass unchanged against production with reset disabled. The final review records this limitation and the stale runtime-config sanity assertion explicitly. Allure history and full tagged HR journeys remain deferred.

## Submission

The submission includes this repository, a public synthetic-data deployment, test/report artifacts, committed design and AI notes, and a demo video. The repository is private; grant reviewer access before sharing its link.

Track remaining external setup in [`docs/submission-checklist.md`](docs/submission-checklist.md). Use [`docs/demo-script.md`](docs/demo-script.md) to record the public walkthrough after deployment and CI verification.

## Reviewer documents and demo

- [Product overview](docs/submission-overview.md), with a [shareable PDF](output/pdf/Salary%20Management%20Assessment%20Overview.pdf).
- [Technical guide](docs/technical-guide.md), with a [PDF covering architecture, schema, APIs, salary rules, tests, and setup](output/pdf/Salary%20Management%20Technical%20Guide.pdf).
- [Demo transcript](docs/demo-transcript.md) and [two-minute walkthrough](docs/demo-script.md).
- [Submission checklist](docs/submission-checklist.md) and [email reply draft](docs/submission-email.md).

The recorded demo uses the local application and fictional records, with narration and captions. Its MP4 is a separate submission attachment. Public app and video sharing URLs must be added after deployment and upload are verified. The PDFs describe the verified application at `1dc488e`; the subsequent Docker ignore fix does not change feature behavior.
