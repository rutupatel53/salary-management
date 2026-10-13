# AI work note: local foundation

## Prompt / instruction

Implement the approved PostgreSQL/Flyway foundation, keep the smallest approved schema, add the health endpoint and a focused test, and make Docker Compose run PostgreSQL plus the API.

## Review finding

The previous scaffold used H2 and a single annual salary field. That conflicts with the approved schema and multi-component salary history, so the legacy backend classes were removed before adding the initial Flyway schema.

## Human decision applied

PostgreSQL stores generated `gross` and `net` values. The `net` expression uses the base amounts because PostgreSQL generated expressions cannot reference another generated column. The activity table is present from the first migration.

## Verification

Maven test execution was attempted. The local Maven cache is outside the writable workspace, and Maven Central DNS is unavailable from this environment. The build could not resolve the Spring Boot parent, so the JUnit test is not yet verified here. Docker Compose is configured, but the local Docker engine is not currently available.
