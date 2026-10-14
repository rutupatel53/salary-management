# Salary Management Assessment

An HR salary-management web app for 10,000 fictional employees across five countries. The approved product scope and system design are in [`docs/requirements.md`](docs/requirements.md) and [`docs/design.md`](docs/design.md). The original assessment is preserved in [`docs/assessment-brief.md`](docs/assessment-brief.md).

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
2. In Netlify, import this repository as a site. The root [`netlify.toml`](netlify.toml) sets the `frontend` base directory, Node 22.22.3, build command, publish folder, and `/api/*` proxy to Render. Subsequent GitHub pushes auto-deploy through Netlify.
3. Verify `https://salary-management-api.onrender.com/api/health`, then open the assigned Netlify site and confirm the employee and insights screens load. Check that the seeded employee filter reports exactly 10,000 rows.
4. The repository is private. Add the assessment reviewer as a GitHub collaborator before sharing the repository URL. Do not add real employee or salary data.

Render's free PostgreSQL tier is temporary and the free API may sleep. Use the published app promptly for the assessment demo; check the current provider limits before provisioning.

## Submission

The submission includes this repository, a public synthetic-data deployment, test/report artifacts, committed design and AI notes, and a demo video. The repository is private; grant reviewer access before sharing its link.

Track remaining external setup in [`docs/submission-checklist.md`](docs/submission-checklist.md). Use [`docs/demo-script.md`](docs/demo-script.md) to record the public walkthrough after deployment and CI verification.
