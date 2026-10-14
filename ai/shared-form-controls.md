# Shared form controls

## User request

Apply the accepted text-field appearance to every page and improve dropdown lists through common components.

## Implementation

- Added reusable `TextFieldComponent` and `DropdownComponent` under `shared/form-controls`.
- Both use one Angular Forms accessor for model updates, touch state, disabled behavior, server errors, and reactive validation.
- Labels sit above a rounded white 48px control. Existing input test IDs stay on the actual input or dropdown trigger.
- The dropdown keeps Material Select's keyboard navigation, type-ahead selection, and overlay positioning. The list uses rounded options, clear hover/selection feedback, and a bounded scroll area.
- Added a pure options pipe for country, department, template, and insight reference lists.
- Replaced all feature inputs and dropdowns, including employee and salary-template dialogs. Styled the pagination option list consistently.
- Template changes now mark the editor dirty on model changes, including dropdown changes, so cancel confirmation covers unsaved selection edits.

## Checks

- Added unit coverage for reactive input updates, touched state, numeric values, required errors, disabled dropdowns, external model updates, and reference-option labels.
- Added a Playwright check for keyboard country selection and employee filtering. Existing feature journeys cover employee creation/editing, template creation/assignment, export filters, and validation.
- Production build passes. All 17 frontend unit tests and all 14 Playwright functional tests pass.
- Browser review at 390px confirms the employee dialog's eight fields are each 48px tall and the page does not overflow horizontally. A desktop dropdown preview is saved in the ignored `e2e/reports/shared-dropdown-desktop.png` artifact.
- The first browser run exposed an existing tooltip assertion matching a hidden transition overlay. The assertion now waits for exactly one visible matching tooltip; the salary journey passes.
