# Performance check

## Dataset and access pattern

The seed creates 10,000 employees and 10,000 current salary versions. Employee pages and filtered exports share the same active employee specification and use stable server-side pages of at most 500 rows for exports. Each export page fetches the latest effective salary values as a bounded database query. Pay summaries and IQR bounds are computed in PostgreSQL.

## Measurements

Run `npm run perf:check` from `/e2e` with `DB_URL` pointed at the seeded database. The script writes `e2e/reports/performance-summary.txt` with `EXPLAIN ANALYZE` plans for an active country-filtered employee page, country/currency pay summary, and job-title/country/currency outlier groups. CI uploads this report with the test artifacts.

On 2026-10-14, the local PostgreSQL 16.15 Docker database returned 25 employee rows in **1.819 ms**, country/currency aggregates in **24.857 ms**, and outlier groups in **29.732 ms**. The database had 10,000 seeded employees plus six fixed Playwright employees. PostgreSQL used an employee-number index for the paged employee query and sequential scans plus in-memory sorts for the full aggregate queries; their measured memory use stayed below 1 MB. These are local warm-cache timings, not hosted performance guarantees. At this size, they do not justify extra indexes or extensions. Recheck the CI plan artifact after any schema or query change.

## Indexes already selected

- `(archived_at, employee_number)` for active directory ordering and paging.
- Country and department foreign-key indexes for filters.
- `(employee_id, effective_date DESC)` for current and historical salary lookups.
- Activity time and employee/time indexes for the newest-first feed.

Do not add a trigram extension or cache without measured need.
