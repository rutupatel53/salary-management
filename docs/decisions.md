# Decision Log

Record confirmed decisions here so important chat context stays with the repository. Do not silently turn a proposal into a requirement. Add the date, decision, reason, and affected docs/commits when a choice is confirmed.

## Confirmed by the user

| Date | Decision | Reason or context |
|---|---|---|
| 2026-10-14 | Assessment is for a Java 17, Spring Boot, PostgreSQL, React, and TypeScript application. | Matches the engineer's existing stack and the assessment. |
| 2026-10-14 | Primary user is the HR manager maintaining salary data for 10,000 employees across countries. | Replaces spreadsheet-based salary management and answers questions about organizational pay. |
| 2026-10-14 | Product scope has four modules: people/departments; salary structure; Excel import/export; pay insights. | Explicit product decision from the user. Full details are in `requirements.md`. |
| 2026-10-14 | Salary structure includes basic pay, allowances, deductions, calculated gross/net, and a simple change history. | User-defined module scope. Calculation rules remain to be agreed. |
| 2026-10-14 | Insights include average, median, range by country, department, and job title, plus outliers within a role. | User-defined product requirement. Statistical definitions remain to be agreed. |
| 2026-10-14 | Exclude payroll runs, tax, payslips, approval workflows, and role-based login. | Payroll is a separate product. Keep the focus on understanding how the organization pays people. |
| 2026-10-14 | Write the requirements document first and make it the first commit. | Required to show product framing before implementation. |
| 2026-10-14 | Add `AGENTS.md`, request a no-code design plan, and save the agreed architecture in Markdown before implementation. | Do not start coding until the user and Codex agree on the architecture. |
| 2026-10-14 | Keep decisions and meaningful AI/build work in Markdown files in the repository. | Preserve chat decisions and implementation rationale for review and interview discussion. |
| 2026-10-14 | Use incremental Git commits, with tests alongside features. | Make the engineering process reviewable and maintain quality. |

## Proposals awaiting agreement

| Proposal | Why it may fit | Decision needed |
|---|---|---|
| Use a Spring Boot modular monolith with four feature packages and one PostgreSQL database. | Simple transactions and deployment for a 10,000-person assessment, with visible module boundaries. | Confirm or revise the architecture. |
| Store salary changes as effective-dated versions with component rows. | Gives a clear history without implementing payroll. | Confirm history behavior and effective-date rules. |
| Calculate gross as basic plus allowances, and net as gross minus deductions. | A simple starting formula. | Confirm whether deductions are positive inputs and whether net may be negative. |
| Group all monetary insights by currency and do not convert currencies. | Prevents misleading cross-currency comparisons. | Confirm any reporting currency or conversion requirement. |
| Define outliers with an agreed statistical method and minimum group size. | Avoids unstable labels for small samples. | Choose IQR, z-score, or another rule and define minimum count. |
| Preview imports before explicit confirmation. | Lets HR inspect row-level problems before updates. | Decide whether valid rows can be confirmed when other rows are invalid, and whether the batch is atomic. |
| Use Docker Compose for local development and choose hosting after the app is ready. | Makes setup repeatable without assuming a hosting account. | Choose deployment target and demo access boundary. |

## Open questions to resolve before feature code

1. Which employee fields are required? Is employee number unique and immutable? Should the app store contact details?
2. Are salaries annual, monthly, hourly, or configurable by pay frequency?
3. What precision and rounding rule apply to each currency and component?
4. Do deductions enter as positive amounts to subtract, or signed amounts? Can calculated net be negative?
5. How should salary history handle backdated changes and overlapping effective dates?
6. What qualifies as an outlier, and what minimum group size is meaningful?
7. Should import be all-or-nothing or allow valid rows when some rows fail? What file size and row count should be supported?
8. What access boundary should the deployed demo use when role-based login is excluded? Use synthetic data only until this is agreed.
9. Which hosting platform is available, and who provides its credentials?

## Implementation record

For each accepted feature, link the requirement, implementation commit, tests run, and any change to this log. Keep AI notes in `/ai`, using the template in `ai/README.md`.

## Workspace status note

An exploratory Angular/H2 scaffold was started before the user confirmed the Java/PostgreSQL/React stack and four-module scope. It has not been reviewed, tested, or approved. Do not extend or commit it. After architecture agreement, decide whether to replace or remove that scaffold as part of the setup commit. The requirements, architecture draft, and this log are the source of truth in the meantime.
