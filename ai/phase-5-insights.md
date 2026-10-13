# AI work note: pay insights

## Prompt / task

Implement currency-separated averages, medians, minimums, maximums, ranges, and IQR outliers. Aggregate in PostgreSQL over current salary versions, group outliers by job title + country + currency, and require at least five records.

## Review findings

- Filtering outlier calculation rows by department would silently change the agreed job-title/country/currency cohort. Department filtering now narrows returned employee rows only; it does not create separate outlier groups.
- Effective-dated salary history means insights must select the latest version whose effective date is on or before the current date. Future salary versions do not appear early.
- The first production build exceeded its original bundle warning budget. Routes now lazy-load each feature screen to reduce the initial bundle.

## Decisions applied

Use PostgreSQL `avg`, `percentile_cont`, `min`, `max`, and generated `gross` columns. Whitelist the group dimension before constructing SQL. Never combine currencies.

## Verification

Backend unit tests inspect the currency grouping, generated gross source, median SQL, IQR formula, and minimum sample size. The Angular production build and Playwright TypeScript checks pass. Direct database insight checks need Docker/PostgreSQL running.
