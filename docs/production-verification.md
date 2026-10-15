# Production verification commands

Updated: 15 October 2026. Do not send database credentials or full connection strings in chat. Production SQL and seed/restart results remain pending until the user supplies evidence.

## Read-only SQL

Run [scripts/verify-production.sql](../scripts/verify-production.sql) in your SQL client connected to the intended Render database. The script starts with `SET default_transaction_read_only = on;`; all subsequent statements are SELECT queries, including SELECT queries with CTEs. Use a read-only database account where available. No reset, insert, update, delete, migration, or schema statements are included.

If your local client already has a secure connection configured, run `psql -v ON_ERROR_STOP=1 -f scripts/verify-production.sql`. Select the database through your existing client configuration; do not add credentials to Git or paste them into chat. Return only the labelled result rows. Every SELECT returns one row with `check_name`, `result`, and `expected`.

The all-history checks include archived records. Active totals exclude archived employees. Insight samples select the latest salary effective on or before `CURRENT_DATE`, exactly like the application. SQL also reports employees with no salary version, current currency mismatches, invalid generated totals, and Flyway migration results. If a sample group has changed since verification, compare it with the live API at the same time rather than forcing the untouched-seed expected values.

## API and reset-route protection

Use `curl.exe` on Windows PowerShell or `curl` on other shells:

```sh
curl -i --max-time 90 https://salary-management-1vd3.onrender.com/api/health
curl -i --max-time 90 -X POST https://salary-management-1vd3.onrender.com/api/test/reset
```

Health must return 200 with `status: UP`. The reset POST must return 404 or 403. This pass observed 200 and 404 respectively. Do not enable test reset support in production to make fixture-dependent tests pass.

## Actuator exposure

```sh
curl -i https://salary-management-1vd3.onrender.com/actuator/health
curl -i https://salary-management-1vd3.onrender.com/actuator
curl -i https://salary-management-1vd3.onrender.com/actuator/env
curl -i https://salary-management-1vd3.onrender.com/actuator/metrics
curl -i https://salary-management-1vd3.onrender.com/actuator/beans
curl -i https://salary-management-1vd3.onrender.com/actuator/mappings
curl -i https://salary-management-1vd3.onrender.com/actuator/configprops
```

Current result: all seven paths return 404. The project has no Actuator dependency; health is the custom `/api/health` controller. Sensitive Actuator endpoints are unavailable, but the requested `/actuator/health` availability check is not satisfied. This verification does not add that feature.

## CORS

```sh
curl -i -X OPTIONS https://salary-management-1vd3.onrender.com/api/employees -H "Origin: https://superlative-blancmange-ebe430.netlify.app" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: content-type"
curl -i -X OPTIONS https://salary-management-1vd3.onrender.com/api/employees -H "Origin: https://untrusted.example" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: content-type"
curl -i https://salary-management-1vd3.onrender.com/api/employees?page=0 -H "Origin: https://untrusted.example"
```

The Netlify preflight must return its exact origin in `Access-Control-Allow-Origin`, never `*`. The foreign-origin responses must have no `Access-Control-Allow-Origin` header. A 403 rejection is expected. The actual preflight checks passed for both origins, and an actual foreign-origin GET returned 403 with no allow-origin header. Testing one foreign origin is a behavioral sample, not proof of every Render environment value; verify that `FRONTEND_ORIGIN` contains only the intended Netlify origin.

## Compare the two insight samples

```sh
curl -sS "https://salary-management-1vd3.onrender.com/api/insights/pay?groupBy=jobTitle&country=IN&jobTitle=Software%20Engineer"
curl -sS "https://salary-management-1vd3.onrender.com/api/insights/pay?groupBy=jobTitle&country=US&jobTitle=Software%20Engineer"
```

For each live sample this pass observed: employeeCount 200, average 4050.50, median 3950.00, minimum 3000.00, maximum 25000.00. Currencies are INR and USD respectively. These match the isolated local seed SQL. They are not yet confirmed against production SQL.

## Seed disabled and restart evidence

1. In the Render web service's Environment settings, confirm `APP_SEED_ENABLED=false`. The code does not read `APP_SEED_ON_STARTUP`, so setting that older name to false proves nothing.
2. Run the read-only SQL file and retain its results before a controlled restart. Do not restart production during another user's work.
3. Restart the service yourself. Check logs for normal Flyway validation/up-to-date messages, `Tomcat started`, and `Started SalaryManagementApplication`, without a database or startup error. These messages confirm startup, not seed behavior.
4. Run the SQL again. Compare counts, migrations, and sample aggregates with the prior output.
5. The current seed runner has no dedicated start/completion log message. Absence of a seed message cannot prove it did not run. Unchanged row counts alone also cannot prove it, because the runner uses idempotent conflict-safe inserts. The environment flag plus code condition establishes that the runner is disabled; before/after results establish no observed data change.

This pass restarted only an isolated local API with seeding enabled: employee and salary-version counts remained 10,000 each. That proves local repeatability, not the production flag or restart outcome.

## Existing Playwright suites

The final report records the exact production execution and failure reasons. Existing functional fixtures require the reset endpoint and fail at setup when it is protected. Three direct-SQL tests were excluded from the production run because production credentials are intentionally unavailable. Their SQL checks were instead run on the isolated local PostgreSQL seed. Do not point `DB_URL` at a local database while claiming to verify production.
