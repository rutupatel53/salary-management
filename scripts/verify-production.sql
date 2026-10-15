SET default_transaction_read_only = on;

-- Run against the intended database with a read-only account where available.
-- Each SELECT returns exactly one row: check_name, result, expected.
-- Identity/reference/history checks cover archived records too; active totals do not.

SELECT 'active_employee_count' AS check_name,
       count(*)::text AS result,
       '10000'::text AS expected
FROM employees WHERE archived_at IS NULL;

SELECT 'duplicate_employee_numbers' AS check_name,
       count(*)::text AS result, '0'::text AS expected
FROM (SELECT employee_number FROM employees
      GROUP BY employee_number HAVING count(*) > 1) duplicates;

SELECT 'duplicate_emails_case_insensitive' AS check_name,
       count(*)::text AS result, '0'::text AS expected
FROM (SELECT lower(work_email) FROM employees
      GROUP BY lower(work_email) HAVING count(*) > 1) duplicates;

SELECT 'employees_with_unknown_country_or_department' AS check_name,
       count(*)::text AS result, '0'::text AS expected
FROM employees e
LEFT JOIN countries c ON c.country_code = e.country_code
LEFT JOIN departments d ON d.department_id = e.department_id
WHERE c.country_code IS NULL OR d.department_id IS NULL;

SELECT 'salary_versions_negative_net_or_null_amounts' AS check_name,
       count(*)::text AS result, '0'::text AS expected
FROM salary_versions
WHERE net < 0 OR net IS NULL OR gross IS NULL
   OR basic_amount IS NULL OR allowances_amount IS NULL OR deductions_amount IS NULL;

SELECT 'salary_versions_invalid_generated_totals_or_negative_inputs' AS check_name,
       count(*)::text AS result, '0'::text AS expected
FROM salary_versions
WHERE basic_amount < 0 OR allowances_amount < 0 OR deductions_amount < 0
   OR gross <> basic_amount + allowances_amount
   OR net <> gross - deductions_amount;

SELECT 'employees_with_no_salary_version' AS check_name,
       count(*)::text AS result, '0'::text AS expected
FROM employees e
WHERE NOT EXISTS (SELECT 1 FROM salary_versions s WHERE s.employee_id = e.employee_id);

WITH current_salary AS (
    SELECT DISTINCT ON (employee_id) employee_id, currency_code
    FROM salary_versions WHERE effective_date <= CURRENT_DATE
    ORDER BY employee_id, effective_date DESC
), groups AS (
    SELECT e.country_code, c.currency_code, count(*) AS active_employees,
           count(s.employee_id) AS employees_with_current_salary
    FROM employees e JOIN countries c USING (country_code)
    LEFT JOIN current_salary s USING (employee_id)
    WHERE e.archived_at IS NULL
    GROUP BY e.country_code, c.currency_code
)
SELECT 'active_counts_per_country_and_currency' AS check_name,
       coalesce(jsonb_agg(to_jsonb(groups) ORDER BY country_code)::text, '[]') AS result,
       'Seed baseline: CA/CAD, DE/EUR, GB/GBP, IN/INR, US/USD each have 2000 active employees and 2000 current salaries; compare live counts with API'::text AS expected
FROM groups;

WITH current_salary AS (
    SELECT DISTINCT ON (employee_id) employee_id, currency_code
    FROM salary_versions WHERE effective_date <= CURRENT_DATE
    ORDER BY employee_id, effective_date DESC
)
SELECT 'current_salary_currency_mismatches' AS check_name,
       count(*)::text AS result, '0'::text AS expected
FROM employees e JOIN countries c USING (country_code)
JOIN current_salary s USING (employee_id)
WHERE s.currency_code <> c.currency_code;

WITH current_salary AS (
    SELECT DISTINCT ON (employee_id) employee_id, gross, currency_code
    FROM salary_versions WHERE effective_date <= CURRENT_DATE
    ORDER BY employee_id, effective_date DESC
)
SELECT 'insight_Software_Engineer_IN_INR' AS check_name,
       jsonb_build_object('jobTitle', 'Software Engineer', 'country', 'IN', 'currency', 'INR',
           'employeeCount', count(*), 'average', round(avg(s.gross), 2),
           'median', round((percentile_cont(0.5) WITHIN GROUP (ORDER BY s.gross))::numeric, 2),
           'minimum', min(s.gross), 'maximum', max(s.gross))::text AS result,
       'Compare API groupBy=jobTitle&country=IN&jobTitle=Software%20Engineer, INR row; untouched seed: count=200 avg=4050.50 median=3950.00 min=3000.00 max=25000.00'::text AS expected
FROM employees e JOIN current_salary s USING (employee_id)
WHERE e.archived_at IS NULL AND e.job_title = 'Software Engineer'
  AND e.country_code = 'IN' AND s.currency_code = 'INR';

WITH current_salary AS (
    SELECT DISTINCT ON (employee_id) employee_id, gross, currency_code
    FROM salary_versions WHERE effective_date <= CURRENT_DATE
    ORDER BY employee_id, effective_date DESC
)
SELECT 'insight_Software_Engineer_US_USD' AS check_name,
       jsonb_build_object('jobTitle', 'Software Engineer', 'country', 'US', 'currency', 'USD',
           'employeeCount', count(*), 'average', round(avg(s.gross), 2),
           'median', round((percentile_cont(0.5) WITHIN GROUP (ORDER BY s.gross))::numeric, 2),
           'minimum', min(s.gross), 'maximum', max(s.gross))::text AS result,
       'Compare API groupBy=jobTitle&country=US&jobTitle=Software%20Engineer, USD row; untouched seed: count=200 avg=4050.50 median=3950.00 min=3000.00 max=25000.00'::text AS expected
FROM employees e JOIN current_salary s USING (employee_id)
WHERE e.archived_at IS NULL AND e.job_title = 'Software Engineer'
  AND e.country_code = 'US' AND s.currency_code = 'USD';

SELECT 'flyway_history' AS check_name,
       jsonb_build_object('failedMigrations', count(*) FILTER (WHERE NOT success),
           'successfulVersionedMigrations', count(*) FILTER (WHERE success AND version IS NOT NULL),
           'versions', coalesce(jsonb_agg(jsonb_build_object('version', version,
               'description', description, 'success', success) ORDER BY installed_rank), '[]'::jsonb))::text AS result,
       'failedMigrations=0; successful versioned migrations 1, 2, 3'::text AS expected
FROM flyway_schema_history;
