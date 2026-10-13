import { mkdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import pg from 'pg';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const output = path.join(root, 'reports', 'performance-summary.txt');
const pool = new pg.Pool({ connectionString: process.env.DB_URL ?? process.env.DATABASE_URL ?? 'postgresql://salary:salary@127.0.0.1:5432/salary_management' });
const checks = [
  ['Active employee country filter and page', `EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT) SELECT employee_id,employee_number FROM employees WHERE archived_at IS NULL AND country_code='IN' ORDER BY employee_number,employee_id LIMIT 25 OFFSET 100`],
  ['Current salary aggregate by country and currency', `EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT) WITH current_salary AS (SELECT DISTINCT ON (employee_id) employee_id,gross,currency_code FROM salary_versions WHERE effective_date<=CURRENT_DATE ORDER BY employee_id,effective_date DESC) SELECT e.country_code,s.currency_code,count(*),avg(s.gross),percentile_cont(0.5) WITHIN GROUP (ORDER BY s.gross) FROM employees e JOIN current_salary s USING(employee_id) WHERE e.archived_at IS NULL GROUP BY e.country_code,s.currency_code`],
  ['Role outlier group calculation', `EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT) WITH current_salary AS (SELECT DISTINCT ON (employee_id) employee_id,gross,currency_code FROM salary_versions WHERE effective_date<=CURRENT_DATE ORDER BY employee_id,effective_date DESC) SELECT e.job_title,e.country_code,s.currency_code,percentile_cont(0.25) WITHIN GROUP (ORDER BY s.gross),percentile_cont(0.75) WITHIN GROUP (ORDER BY s.gross) FROM employees e JOIN current_salary s USING(employee_id) WHERE e.archived_at IS NULL GROUP BY e.job_title,e.country_code,s.currency_code HAVING count(*)>=5`]
];
try {
  const sections = [];
  for (const [name, sql] of checks) {
    const result = await pool.query(sql);
    sections.push(`${name}\n${result.rows.map(row => row['QUERY PLAN']).join('\n')}`);
  }
  await mkdir(path.dirname(output), { recursive: true });
  await writeFile(output, `Environment: seeded synthetic 10,000-employee PostgreSQL database\nCaptured: ${new Date().toISOString()}\n\n${sections.join('\n\n')}`);
  console.log(`Performance query plans saved to ${output}`);
} finally { await pool.end(); }
