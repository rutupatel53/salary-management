# Initial documentation and commit plan

Keep the history small and meaningful. Do not combine the requirements, design agreement, infrastructure, and feature work into one large commit.

## Documentation-first commits

1. `docs: define salary management requirements`
   - Add `requirements.md` with the goal, four modules, exclusions, and acceptance criteria.
2. `docs: add Codex project rules and decision log`
   - Add `AGENTS.md`, `docs/decisions.md`, and `ai/README.md`.
3. `docs: capture architecture draft and open decisions`
   - Save the no-code plan in `docs/design.md` and keep the component view in `docs/architecture.md`. Mark both as drafts until the user reviews them.

Review these documentation changes before application code. The current architecture is a draft and must not be treated as approval. Update the decision log and design docs to reflect the user's feedback.

## Setup commit after architecture approval

4. `chore: add application and database foundation` (only after architecture approval)
   - Add Java and React project skeletons, Docker Compose with PostgreSQL, Flyway wiring, local configuration examples, and a health check.
   - Do not include employee or salary features in this commit.

## Feature commits after setup

Follow the reviewed feature order in `docs/architecture.md`: people and departments; salary structure and history; seed data; pay insights; Excel import/export; React screens; performance check; deployment and demo. Give each feature a focused commit with its tests and an `/ai` note. Adjust the order only after recording the reason.
