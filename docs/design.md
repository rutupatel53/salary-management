# No-Code Design Plan

**Status: draft for user review. This plan contains no implementation. Do not start application code until the user confirms the architecture and the decisions for the first slice.**

This plan preserves the confirmed scope in [`requirements.md`](../requirements.md). The architecture diagram and module boundaries are in [`architecture.md`](architecture.md). Confirmed choices, proposals, and unresolved questions are in [`decisions.md`](decisions.md).

## 1. Product shape

Build one web application for the HR manager. Keep four modules visible in the user experience and code: people and departments, salary structure, Excel import/export, and pay insights. Use Java 17, Spring Boot, PostgreSQL, React, and TypeScript. Seed the demo with 10,000 deterministic fictional employees.

Do not add payroll runs, tax, payslips, approval workflows, or role-based login. These are outside the agreed scope. Because salary data is sensitive, use synthetic data only until the hosting and access boundary are agreed.

## 2. Proposed architecture

Use a Spring Boot modular monolith with a React single-page application and PostgreSQL. Keep modules in one deployable backend so salary updates, imports, and history can use database transactions without service-to-service complexity. Use Flyway migrations for schema changes. Run PostgreSQL and the application locally with Docker Compose.

The browser calls JSON endpoints for directory, salary, and insights workflows. It uploads workbooks to a preview endpoint and downloads templates and filtered exports through API endpoints. The API validates all input, owns business calculations, and returns DTOs. PostgreSQL performs filtering, pagination, and aggregate queries.

## 3. Proposed data and calculation model

- `Employee` references a department, country, and job title. Store only fields justified by the HR workflow.
- A salary structure has an effective date, currency, and versioned components.
- A component has a type: basic, allowance, or deduction, plus a label and decimal amount.
- The initial calculation proposal is `gross = basic + allowances` and `net = gross - deductions`.
- Preserve previous salary structures so HR can inspect changes. Do not model payroll periods or payslips.
- Use Java `BigDecimal` and PostgreSQL `NUMERIC`. Keep currencies separate in both records and analytics.

These are proposals until precision, sign, rounding, effective-date, and negative-net rules are agreed.

## 4. Proposed workflow and API boundaries

1. The employee directory sends search, country, department, job-title, sort, page, and page-size criteria. The server applies filters and bounded pagination.
2. Employee and department forms call validated create/update endpoints. Request and response DTOs isolate the API from persistence models.
3. Salary forms submit component values and effective dates. The server calculates totals and appends a salary version in one transaction.
4. Excel upload creates a preview with row numbers and field-level errors. Persisting changes is a separate confirmation action.
5. Export applies the directory's filters and streams results. Do not fetch every employee into application memory.
6. Pay insight requests choose dimensions and filters. PostgreSQL returns average, median, and range grouped with currency. The outlier rule is applied within a defined role group after the user agrees on its method and minimum sample size.

Route examples are in [`architecture.md`](architecture.md). Treat them as examples until the data contract is approved.

## 5. Build order after approval

1. Commit the requirements document first.
2. Commit `AGENTS.md`, the decision log, AI note template, and reviewed design documents.
3. Add only project foundations: Java and React skeletons, Docker Compose with PostgreSQL, Flyway, configuration examples, and health check.
4. Deliver people and department CRUD with validation and tests.
5. Deliver salary components, calculations, and change history with tests.
6. Add the deterministic 10,000-person seed.
7. Add pay insight queries and tests.
8. Add Excel preview, error report, confirmation, and filtered export with tests.
9. Add React screens as small vertical slices, with component and API tests.
10. Check indexes and representative query performance using seeded data.
11. Deploy, verify the workflows, update README, and record the demo video.

Make one focused commit per completed slice. Update the Markdown decision log and add an `/ai` note when a task changes a decision or adds a meaningful feature.

## 6. Test approach

- Test salary formulas, decimal precision, rounding, validation, and history behavior.
- Test employee and department uniqueness, filters, sorting, and pagination limits.
- Test exactly 10,000 deterministic seed rows and relationships.
- Test average, median, range, and outlier boundaries with small, empty, and multi-currency groups.
- Test Excel parsing, row-level errors, preview isolation, confirmation behavior, and filtered export.
- Add UI tests for form errors, loading/empty/error states, filters, preview, and summary labels.

Use fixed fixtures. Avoid random test values. Do not test with real personal or compensation data.

## 7. Decisions needed for sign-off

Resolve the questions in [`decisions.md`](decisions.md) before locking the first feature contract. Start with required employee fields, pay frequency, component sign/rounding rules, salary history dates, outlier method/minimum group size, import transaction behavior, and demo hosting/access. Once the user confirms this plan, update the documents and proceed to the setup-only commit. Until then, do not implement code.
