# Submission documents and recorded demo

Date: 2026-10-15

## Prompt used

The user asked for a shareable document, a technical document with architecture and implementation details, and a two-minute demo video. The user is handling public deployment separately.

## Approach

Read the implemented APIs, UI flows, schema, seed rules, deployment configuration, and latest successful CI evidence. Prepare a concise two-page overview and a nine-page technical guide, with vector system and database diagrams. Keep Markdown companions in docs. Record the actual local UI journey with fictional records, then add an offline synthetic narration, chapter labels, and optional subtitles.

## Errors or omissions corrected

- Existing handover notes had stale test counts. The verified application CI run reports 33 backend, 17 Angular, and 23 Playwright tests.
- Public deployment is not yet verified. Documents and video explicitly distinguish local evidence from the hosted target.
- Department administration is not implemented. The guides state that departments are seeded references.
- The seed entry point is a Java startup runner, not a separate shell script. The guide describes the actual command and repeatable behavior.
- Exports page database reads but return the final workbook as a byte array. The guide states that memory limit rather than claiming a fully streamed HTTP response.

## Checks

Rendered and inspected every page of both PDFs. Recorded a real invalid/valid import, template creation, salary assignment, country insights, filtered export, and activity review. Verified MP4 duration is exactly 120 seconds, with 1280x720 H.264 video, AAC audio, and English subtitles. Inspected frames from every chapter. No application feature code was changed for artifact creation.

## Handover

Documents reference CI run https://github.com/rutupatel53/salary-management/actions/runs/38616293398 at application commit 1dc488e. The later backend Docker ignore fix is 5b4bbca. Public URLs, final hosted verification, reviewer access, and the email reply remain with the user. Keep the video and generated reports as separate submission artifacts.
