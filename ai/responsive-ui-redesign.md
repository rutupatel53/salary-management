# Responsive UI redesign

## User direction and correction

The initial shell used a desktop sidebar and mobile drawer. The user asked for a different overall UI and then clarified the desired final behavior: use a side menu opened by a hamburger on both web and mobile. Keep the menu closed by default so it does not take screen space, and make it close after choosing a route, pressing Escape, or clicking the backdrop.

## Changes

- Added a compact top bar with a hamburger and a responsive overlay side menu for all screen sizes.
- Added compact page-specific usage steps across the employee, salary, insights, import/export, and activity screens. Salary-template edit/copy/assign actions use icons with tooltips and accessible names.
- Refined Angular Material controls after the user said fields and buttons looked unfinished: shared field height, label/input typography, focus colors, button sizes, hover states, disabled feedback, and mobile filter wrapping. Kept salary-template row actions on one line.
- User feedback on the preview showed floating-only labels and uneven one-line spacing were still not polished. Switched all form labels to persistent top labels, made the guide a three-column equal grid with a mobile stack, and aligned the template search/status/action row with explicit grid tracks. Added a search icon and meaningful placeholder.
- Kept blue for the active route and primary actions; kept other surfaces neutral and labels concise.
- Preserved the dedicated salary-template and direct annual-CTC assignment workspaces.
- Added generated `runtime-config.js` to Angular's root build assets. Before this fix, nginx served the HTML fallback for that URL and caused `Unexpected token '<'` on direct page loads.
- Playwright covers opening and closing the menu on phone and desktop, all navigation links, no page overflow, and runtime-config loading.

## What the AI got wrong

The prior attempt kept Material's border-cut labels and a 40px button beside taller fields. The user clarified that the actual controls must have equal height and that they dislike that field variant. Template filters now use separate labels and simple rounded white controls; both fields and Search are exactly 48px high, bottom-aligned in one desktop row.

I treated “responsive” as permission to keep a persistent desktop sidebar, then briefly replaced it with horizontal top navigation after interpreting the feedback too broadly. The user's latest instruction specifies the interaction: hamburger opens a side menu on web and mobile. I have implemented that interaction and saved it as the current decision.

## Verification

- Simple template filter controls: production build passes; browser measurements at 1038px confirm both fields and Search are 48px tall with identical top/bottom coordinates. At 390px, controls stack with equal widths and no page overflow. Search and status filtering were exercised in the running UI and restored afterward.

- PostgreSQL 16.15, Java 17 API, and Angular UI run locally in Docker.
- Flyway applied all three migrations; the deterministic seed contains exactly 10,000 employees.
- Backend JUnit: 28 tests pass. Angular Vitest: 13 tests pass.
- Required Playwright suites: 22 tests pass with zero failures.
- Salary template and assignment screens were checked at 390, 768, and 1440px with no page overflow or browser errors.
- Latest verification: Angular production build succeeded; 13 unit tests passed; Playwright functional passed 13/13 and sanity passed 3/3. The Escape-key close behavior was made explicit after the first desktop test exposed a failure.
