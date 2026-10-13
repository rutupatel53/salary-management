# Project rules for Codex

## Confirm the plan before coding

- Treat `requirements.md`, `docs/architecture.md`, and `docs/decisions.md` as the current project context.
- The architecture is not approved yet. Do not write or extend application code until the user confirms the architecture and the open decisions needed for the next feature.
- If a new request changes scope or a design choice, record it in `docs/decisions.md` and update the related requirements or design note before implementation.
- Keep implementation and AI notes in Markdown so the work remains understandable outside the chat.

## Agreed stack

- Backend: Java 17 and Spring Boot.
- Database: PostgreSQL with Flyway migrations.
- Frontend: React and TypeScript.

## Engineering rules after approval

- Use `BigDecimal` for money in Java and `NUMERIC`/`DECIMAL` in PostgreSQL. Never use `float` or `double` for salary values or calculations.
- Use request and response DTOs at API boundaries. Do not expose JPA entities directly.
- Keep search, sorting, and pagination server-side. Bound page sizes and avoid loading all employees into memory for list or export requests.
- Validate inputs on the server. Return clear, stable error responses.
- Add deterministic tests with every feature. Avoid random test data and test the edge cases for the behavior being changed.
- Work in small vertical slices. Explain the change, run relevant checks, inspect the diff, and make one focused commit per completed slice.
- Do not add excluded features: payroll runs, tax, payslips, approval workflows, or role-based login, unless the user changes scope.
- Never place real personal or salary data, credentials, or secrets in the repository or demo seed.
- Do not silently change agreed architecture or requirements. Propose a decision and record it first.
