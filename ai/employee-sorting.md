# Sorting and export slice

User requested basic sorting and a complete product UI. Added sortable employee headers, a visible job-title filter, and Export view beside Add employee. A shared backend sort DTO maps allowed public fields to JPA paths for both list and export. Unknown fields and directions return 400. IDs break ties.

Added deterministic unit tests for composite name sorting, reference-field mapping, and invalid options. Playwright covers descending server sorting with search retained and matching export request parameters. Existing import/export download code now uses a shared helper.

Verification: 28 backend unit tests, 11 Angular tests, and 18 Playwright tests passed. All seven sort fields returned HTTP 200 against PostgreSQL. Desktop table screenshot inspected.

A regression run exposed a rapid second Cancel click during confirmation close animation. Disabled Cancel during the pending confirmation and marked the component for refresh after it closes. The full suite then passed without sleeps or retries.
