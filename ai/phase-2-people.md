# AI work note: people directory

## Prompt / task

Implement the employee and reference-data vertical slice from the approved design. Use DTOs, MapStruct, service interfaces and implementations, shared specifications, server-side pagination, validation, audit writes, Angular Material, and Playwright sanity checks.

## Review finding

The starter used numeric employee IDs, a single annual-salary field, and H2. Those assumptions did not fit the agreed PostgreSQL schema or fixed seed identity rules. I removed the old employee model and introduced UUID employee IDs with required hire date, country, department, title, and work email.

## Decisions applied

- Employee numbers remain immutable after creation. Email is normalized to lowercase and checked case-insensitively.
- Archive is idempotent and retains employee, salary, and activity records.
- Search and filters run in a shared JPA specification; paging is capped at 100 rows.
- The HR activity actor is the configured assessment actor because login is outside the approved scope.

## Verification

The backend suite passes all 9 tests, including identity normalization, duplicate rejection, salary validation/history, UUID v5, and the deterministic seed generator. The Angular 22.2.2 production build succeeds on Node 22.22.3. Playwright TypeScript compilation passes. The Angular/Vitest builder reports no discovered test files for this Windows workspace path, and direct Vitest cannot initialize Angular TestBed without the Angular builder. Docker Engine is unavailable, so database-backed and browser tests remain pending.
