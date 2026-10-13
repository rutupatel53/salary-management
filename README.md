# Salary Management Assessment

An HR salary-management web app for 10,000 fictional employees across five countries. The approved product scope and system design are in [`docs/requirements.md`](docs/requirements.md) and [`docs/design.md`](docs/design.md). The original assessment is preserved in [`docs/assessment-brief.md`](docs/assessment-brief.md).

## Stack

- Java 17, Spring Boot, Spring Data JPA, Flyway, PostgreSQL
- Angular standalone, Angular Material, strict TypeScript
- Playwright with TypeScript for end-to-end tests
- Docker Compose for local PostgreSQL
- Render for API/PostgreSQL and Netlify for the Angular app; their GitHub integrations auto-deploy. GitHub Actions runs tests only.

## Product modules

1. Employee, department, and country directory
2. Salary structures and append-only effective-dated history
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

Run PostgreSQL and the app with Docker Compose. Exact commands and environment variables will be documented with the setup slice. The application uses synthetic data only. The seed command creates exactly 10,000 employees; the smaller Playwright fixture set is separate.

## Tests

Backend unit tests use JUnit 5 and Mockito. Angular unit tests use the Angular CLI supported runner. Playwright scripts cover `@sanity`, `@functional`, `@data`, and (time permitting) `@e2e`. CI runs tests and saves reports; deploys are handled by Render and Netlify GitHub auto-deploy.

## Submission

The submission includes this repository, a public synthetic-data deployment, test/report artifacts, committed design and AI notes, and a demo video. The repository is private; grant reviewer access before sharing its link.
