# Salary Management Assessment Overview

Prepared for Incubyte by Rutu. Updated 15 October 2026.

Java and Angular take-home assessment for Incubyte. Prepared by Rutu.

ACME Salary Management helps an HR manager replace spreadsheet-based salary tracking for 10,000 employees across five countries. HR can maintain employee records, assign reusable salary templates, validate spreadsheet imports, and answer pay questions within each currency.

## What the application supports

| Area | What HR can do |
| --- | --- |
| People | Search, filter, sort, and page employees. Add and edit profiles; archive without deleting salary history. Choose seeded country and department references. |
| Salary templates | Define country-specific basic pay, allowances, employer contributions, and deductions. Save drafts, activate templates, and copy existing templates. |
| Salary assignment | Find an employee directly, enter annual CTC and an effective date, preview monthly and annual amounts, and confirm a new salary snapshot. |
| Excel | Download the template, preview row errors, fix the workbook, confirm an atomic create-only import, and export a filtered employee view. |
| Pay insights | View average, median, minimum, maximum, and range by country, department, or job title. Review outliers within comparable roles. |
| Activity | Review recorded employee, template, salary, import, and export actions with timestamps and a labeled demo actor. |

### Countries and currencies

United States USD, India INR, United Kingdom GBP, Germany EUR, and Canada CAD. Every pay comparison stays separated by currency; the application performs no currency conversion.

### Deliberate scope

Payroll runs, taxes, payslips, approval workflows, and role-based login are excluded. This assessment focuses on salary information and HR pay questions. The public demonstration must contain only fictional data.

## Engineering approach and evidence

The implementation uses Java 17, Spring Boot, Spring Data JPA, Flyway, PostgreSQL 16, and strict standalone Angular with Angular Material. A single API service and a six-table relational schema keep the system understandable.

| Decision | Reason |
| --- | --- |
| Exact salary rules | Java BigDecimal and NUMERIC columns handle money. PostgreSQL generates gross and net and rejects negative net values. |
| Preserved history | Assignments save effective-dated component snapshots. Later template changes do not rewrite previous employee salaries. |
| Database-driven scale | Search, filtering, sorting, paging, and pay aggregation run in PostgreSQL. Exports fetch bounded pages. |
| Stateless imports | Preview writes nothing. Confirmation revalidates the uploaded file and creates all rows or none. |
| Intentional AI use | Codex was used for planning, implementation, and review. Prompts, corrections, decisions, and performance notes are committed alongside incremental feature commits. |

### Verified checks

| Check | Latest verified result |
| --- | --- |
| Backend unit tests | 33 passed in GitHub Actions |
| Angular unit tests | 17 passed in GitHub Actions |
| Playwright | 23 passed across sanity, functional, and data suites |
| Seed data | Exactly 10,000 generated employees, with deterministic IDs and 50 comparable role groups of 200 employees |
| Build and CI | Angular production build passes; latest application commit 1dc488e has a successful test-only CI run |

### Delivery status and limits

The local application and CI are verified. Render API and PostgreSQL plus Netlify UI are the deployment targets; public deployment verification remains a final handover step. Imports are limited to 500 rows and 5 MB per workbook. Departments are currently seeded references rather than an HR administration screen.

The repository is private, so reviewers need collaborator access. The final submission should attach the public app link and the recorded demo link.

Repositoryhttps://github.com/rutupatel53/salary-managementVerified CI runhttps://github.com/rutupatel53/salary-management/actions/runs/38616293398
