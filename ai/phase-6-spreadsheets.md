# Phase 6: spreadsheet import and export

## Prompt used

Implement the approved stateless Excel workflow: provide the template, parse and preview row-level validation without writes, re-upload and revalidate on confirm, atomically create employees and first salary versions, and export the same server-filtered employee view. Include unit and Playwright tests.

## AI mistake found

The initial import validation built an `EmployeeRequest` with a null department ID before looking up the uploaded department. Because department is required by the DTO, every import row would have failed validation.

## Correction

Resolve the active department before validating the employee DTO. Keep required-field and business validation at the API boundary. Add tests for a valid preview, a negative-net row, successful confirm, and rejected confirm with no writes. Use the shared employee specification and bounded 500-row pages for export.

## Decisions followed

- Imports have no database batch or error tables. Preview is read-only; confirm re-uploads the workbook and validates again in the write transaction.
- Import creates only. Any invalid row rejects the entire import.
- Formula cells are not evaluated. Files are bounded to 5 MB and 500 employee rows.
- Exports use the same active employee filters, current salary values, and server-side pagination.
