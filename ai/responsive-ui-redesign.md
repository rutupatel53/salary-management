# Responsive UI redesign

User request: replace the disliked sidebar and color theme, add badges, and make the complete UI responsive. Reference screenshots also clarify that salary templates must be created centrally and assigned using employee CTC.

Changes: light sidebar, blue and neutral theme, icon navigation, active-route state, mobile Material drawer, breadcrumbs, synthetic-data identity label, shared status badges, consistent headings and table styling. Tables scroll inside their cards while paginators remain outside horizontal scrolling. Added a skip link and mobile navigation tests.

CTC calculation rules require user confirmation before changing the salary schema. Proposed annual CTC, fixed/percentage/balancing components, employer contributions distinct from gross, and immutable assignment snapshots. The current UI work does not imply those rules are implemented.

Verification: Angular production build and 11 unit tests pass. All 20 Playwright tests pass after fixing Escape handling during drawer animation. Captured all four main screens at 390, 768, and 1440px; all pass page-overflow checks. Inspected employee desktop/mobile and insights desktop screenshots.
