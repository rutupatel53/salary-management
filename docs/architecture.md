# Salary Management Architecture Draft

**Status: draft for review. No application code should be extended until the user and Codex agree on this design.** Confirmed scope and decisions are in [requirements.md](../requirements.md) and [decisions.md](decisions.md). Open choices are listed in [decisions.md](decisions.md).

## Architecture goals

- Make four product modules clear: people and departments, salary structure, Excel import/export, and pay insights.
- Keep salary values exact and country/currency context visible.
- Support 10,000 employee records with database filtering and pagination.
- Keep the first version small enough to build, test, explain, and demo.

## Proposed system shape

```text
HR manager browser
        │ HTTPS
        ▼
React + TypeScript application
        │ JSON API and file upload/download
        ▼
Spring Boot modular monolith
  ├── People and departments
  ├── Salary structure and change history
  ├── Import and export
  └── Pay insights
        │ Flyway migrations / SQL
        ▼
     PostgreSQL
```

This is a proposal for a modular monolith. It keeps transactions and deployment simple while leaving feature boundaries visible. Separate services, a message broker, and a cache are not justified by 10,000 employees unless measurement or new requirements show a need.

## Proposed module boundaries

### People and departments

Own employee, department, country, and job-title references. Provide create, read, update, search, and filters. List endpoints must use bounded server-side pagination. Store only employee fields confirmed in requirements.

### Salary structure

Represent a dated salary structure as decimal-valued components. The initial proposal is one basic component, zero or more allowances, and zero or more deductions. Calculate totals in the domain/service layer and return them in a response DTO. Preserve prior structures when pay changes. Exact formulas, signs, currency precision, and effective-date rules are open decisions.

### Excel import and export

Use a server-side import workflow: validate a workbook, produce a preview with row numbers and field errors, then persist only after explicit confirmation. Export applies the same filters and sort as the directory and streams the output rather than loading all employees into application memory. The import transaction policy and file limits are open decisions.

### Pay insights

Compute aggregates in PostgreSQL for average, median, and range. Group by country, department, job title, and currency so unlike amounts are not compared. Define an outlier rule within a role and a minimum sample size before implementation. Use bounded filters and return small, typed DTOs.

## Proposed data concepts

- **Employee:** stable employee identifier, name, country, department, and job title. Additional fields require a product reason.
- **Department, Country, JobTitle:** normalized reference data where appropriate. Whether country and job title are controlled lists or editable values needs agreement.
- **SalaryStructure:** employee, currency, effective date, and a version of the component set.
- **SalaryComponent:** type (basic, allowance, deduction), label, and `NUMERIC` amount. Do not store money as floating point.
- **ImportBatch and ImportRow:** temporary validated input, preview errors, and confirmation state. Retention and cleanup policy need agreement.

Use DTOs for API inputs and outputs. Do not serialize persistence entities directly. Use Flyway for schema changes. Name database constraints and indexes for business rules and measured query patterns.

## API shape to review

- `GET /api/employees`: query, filters, sort, page, and page size.
- `POST /api/employees`, `PUT /api/employees/{id}`: employee changes with server-side validation.
- `GET /api/departments`, `POST /api/departments`, `PUT /api/departments/{id}`: department maintenance.
- `GET /api/employees/{id}/salary-structures`, `POST /api/employees/{id}/salary-structures`: current and historical salary structures.
- `POST /api/imports/preview`, `POST /api/imports/{id}/confirm`: preview and confirm workbook records.
- `GET /api/employees/export`: export using the active directory filters.
- `GET /api/insights/pay`: selected dimensions and filters for average, median, range, and outliers.

These route names are examples, not frozen contracts. Agree on the data fields, formulas, and import behavior before fixing the API.

## Delivery and quality approach

Use Docker Compose for local PostgreSQL and the application. Flyway owns schema creation and evolution. Add backend and frontend tests in the same change as each behavior. Use deterministic seed data. Test import and insight behavior against PostgreSQL semantics. Measure representative 10,000-row queries before adding indexes. Keep deployment secrets out of Git.

Because role-based login is out of scope while salary data is sensitive, use only synthetic data until the hosting and access boundary is agreed. Do not expose an unauthenticated demo with real employee data.
