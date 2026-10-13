# Backend review against the reference project

## User request
Review the local emgage_ess-2 backend as a reference for readable services, controllers, common responses, status codes, repositories, and import/export. Ensure PostgreSQL 16 is used.

## What needed correction
The earlier cleanup still left SQL inside insights and spreadsheet services. The reference-data controller also depended directly on repositories. Oversized multipart uploads could receive a generic 500 response.

## Changes
Moved SQL into dedicated repositories. Added a transactional reference service and DTO projections. Kept business transactions in services and required an active transaction for import writes. Added HTTP 413 handling. Retained constructor injection and centralized exception handling. Reference source files were read only.

## Checks
Backend build and 25 unit tests pass. Existing Playwright regression suite is run against the rebuilt API and PostgreSQL 16.15; final outcome is recorded in the commit review response.

## Limits
This refactor preserves current feature behavior. It does not prove complete production readiness. Excel parsing still requires bounded loops, and the completed download bytes are held in memory for the response.
