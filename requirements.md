# Salary Management Requirements

## Goal

Replace spreadsheet-based salary tracking with a web application for the HR manager at an organization with 10,000 employees across countries. Help HR maintain employee pay records and answer clear questions about how the organization pays people.

## Scope

1. **People and departments:** maintain employee, department, country, and job title records. Search, filter, add, and edit employees.
2. **Salary structure:** maintain basic pay, allowances, and deductions. Calculate gross and net pay. Keep a simple salary change history.
3. **Excel import and export:** download an import template, preview uploads, show row-level errors, and export the current filtered view.
4. **Pay insights:** show average, median, and range by country, department, and job title. Identify outliers within a role.

## Deliberately out of scope

Payroll runs are a separate product. Tax rules and payslips vary by country and are not needed to answer the HR manager's pay questions. Approval workflows and role-based login would add policy and process decisions that are not part of this assessment. The demo must use fictional data. Deployment access and data handling still need to be agreed before publishing the app.

## Quality and acceptance

- Use Java 17, Spring Boot, PostgreSQL, React, and TypeScript.
- Seed exactly 10,000 deterministic fictional employees.
- Keep money in decimal types. Never combine different currencies in a statistic without an agreed conversion rule.
- Use DTOs at API boundaries and server-side pagination, search, and filtering.
- Validate requests and imported rows. Explain invalid rows before changing employee records.
- Add fast, deterministic tests with each feature.
- Keep changes small and use incremental commits. Record important decisions and AI work in Markdown.
- Deliver a working app, deployment instructions, a demo video, requirements, architecture notes, and AI-work notes.

**Acceptance:** an HR manager can manage people and salary structures, import and export records with clear feedback, inspect pay summaries, and use the deployed demo. Reviewers can run the tests and follow the design and commit history.
