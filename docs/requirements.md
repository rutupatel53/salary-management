# Salary Management Requirements

## Goal

Replace ACME's spreadsheet-based salary tracking with a web app for HR. Support 10,000 employees across countries and help the HR manager maintain salary records and answer questions about how the organization pays people.

## Scope and features

1. **People and departments:** maintain employees, departments, and supported countries. Search, filter, paginate, add, and edit employees. Archive employees without removing their history. Employee records include employee number, name, work email, hire date, country, department, and job title.
2. **Salary structure:** store basic pay, allowances, and deductions as effective-dated, append-only salary versions. Calculate gross as basic plus allowances and net as gross minus deductions. Reject negative amounts and negative net. Use `BigDecimal` and two decimal places.
3. **Excel import and export:** provide a template and row-level upload preview. Import is create-only and atomic: confirm re-uploads and validates the file, and any invalid row prevents all writes. Export the currently filtered employee view. Do not persist preview batches.
4. **Pay insights:** show average, median, minimum, maximum, and range by country, department, and job title. Show outliers within comparable roles. Keep currencies separate. Initial countries/currencies: US/USD, India/INR, UK/GBP, Germany/EUR, Canada/CAD.
5. **Activity:** record the actor, action, time, and safe summary for employee changes, salary changes, imports, and exports. Provide a read-only activity screen.

## Technical and quality requirements

Build a working backend and UI with Java 17, Spring Boot, Spring Data JPA, Flyway, PostgreSQL, Angular with Angular Material and strict TypeScript, and Playwright end-to-end tests. Use Docker Compose for local setup. Include a deterministic seed script for exactly 10,000 fictional employees, fast deterministic unit tests, and incremental commits. Commit useful design, AI prompt, trade-off, and performance notes. Deploy a public app with synthetic data only and provide a demo video.

## Deliberately out of scope

Do not build payroll runs, tax calculations, payslips, approval workflows, or role-based login. Payroll is a separate product; tax and payslips vary by country; approval and login processes add policy scope. With no login, the public demo must use synthetic data and its audit actor is a configured demo identity, not a verified person.
