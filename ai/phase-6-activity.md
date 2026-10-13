# Phase 6: activity feed

## Prompt used

Add a read-only, newest-first, server-paged activity endpoint and Angular screen. Use existing audit events for employee, salary, import, and export changes. Keep HTTP access in an API service, and add unit and Playwright tests.

## Review and correction

The first mock test used an untyped Mockito `any()` call for the repository page overload. Java resolved it as the wrong `findAll` overload. Type the matcher as `Pageable`, then assert the requested page size and newest-first timestamp ordering.

## Decisions followed

- No login exists in the assessment scope. Activity shows the configured demo actor identifier.
- Activity rows are immutable through the API. The endpoint supports a bounded page and optional action filter.
- Salary values remain in salary history; audit details only need action context and counts.
