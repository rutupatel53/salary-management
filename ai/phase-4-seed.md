# AI work note: deterministic seed

## Prompt / task

Create a repeatable 10,000-person seed for five country/currency pairs and ten job titles. Every job-title/country/currency group needs at least five salaries and at least one clear outlier. Verify exact counts, relationships, salary formulas, and repeatability.

## Review finding

Java's `UUID.nameUUIDFromBytes` creates UUID version 3 (MD5), not the approved UUID version 5 (SHA-1). It was not used. `UuidV5` follows RFC 4122 and has an RFC DNS-namespace known-vector test.

## Decisions applied

- Generate 200 rows in each of 50 job-title/country/currency groups, for exactly 10,000 employees.
- Use stable employee and salary-version IDs derived from employee number and salary effective date.
- Put one synthetic high gross-pay value in each group so the outlier insight has a deterministic example.
- Batch insert and use conflict handling by deterministic ID so reruns do not duplicate seed rows.

## Verification

The backend test suite passes all 9 tests. It verifies 10,000 generated records, 50 groups with 200 each, nonnegative net, and repeatable IDs/values. Playwright TypeScript compilation passes. Database-level seed checks need Docker/PostgreSQL running.
