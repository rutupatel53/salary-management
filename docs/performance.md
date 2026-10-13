# Performance check

## Dataset and access pattern

The seed creates 10,000 employees and 10,000 current salary versions. Employee pages and filtered exports share the same active employee specification and use stable server-side pages of at most 500 rows for exports. Each export page fetches the latest effective salary values as a bounded database query. Pay summaries and IQR bounds are computed in PostgreSQL.

## Measurements

Run `npm run perf:check` from `/e2e` with `DB_URL` pointed at the seeded database. The script writes `e2e/reports/performance-summary.txt` with `EXPLAIN ANALYZE` plans for an active country-filtered employee page, country/currency pay summary, and job-title/country/currency outlier groups. CI uploads this report with the test artifacts.

The local Docker engine was unavailable during implementation, so no local timing numbers are claimed. The first successful GitHub Actions run should be used to review the measured plans. Add indexes only when those plans show a table scan or sort that materially affects the 10,000-row workflows.

## Indexes already selected

- `(archived_at, employee_number)` for active directory ordering and paging.
- Country and department foreign-key indexes for filters.
- `(employee_id, effective_date DESC)` for current and historical salary lookups.
- Activity time and employee/time indexes for the newest-first feed.

Do not add a trigram extension or cache without measured need.
