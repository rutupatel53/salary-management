# Employee modal slice

User direction: deliver a product UI with modal forms and shared confirmation dialogs, informed by the emgage reference project.

The original employee editor was embedded below the table. Replaced it with an Angular Material dialog and a reusable dialog shell. Kept API calls in the feature API service. Added required-field errors, server field errors, pending-save protection, cancel/discard confirmation, and dialog focus restoration.

Tests cover invalid form submission, duplicate-save protection, create/edit/archive, and discard cancellation. Salary-template management and sorting remain separate open checklist gates.

Verified: Angular production build and 11 unit tests pass. All 9 functional Playwright tests pass. Inspected the modal at 1440x1000 and 390x844; narrow layout scrolls fields while keeping actions available.
