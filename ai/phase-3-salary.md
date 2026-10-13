# AI work note: salary history

## Prompt / task

Build effective-dated salary snapshots with BigDecimal components, generated database gross/net columns, validation, append-only history, a salary detail screen, and a focused Playwright functional check.

## Review findings

- PostgreSQL does not allow a generated column expression to reference another generated column. `net` therefore uses the equivalent base expression `basic_amount + allowances_amount - deductions_amount`.
- JPA does not return generated values automatically after insert. The service flushes then refreshes the inserted salary row before mapping the response.
- JavaScript floating point is unsuitable for user-entered currency math. The form accepts decimal strings and computes preview values as integer minor units.

## Decision applied

Salary currency must match the employee's country. New dates must follow the latest version. Inputs round to two decimal places with `HALF_UP`; database checks also enforce nonnegative components and net.

## Verification

The Angular production build succeeds, Playwright TypeScript compilation passes, and the focused integer-minor-unit tests pass. Maven unit tests cover rounding, invalid net, and effective-date order. Browser/database execution still needs the local Docker engine.
