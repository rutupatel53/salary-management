import { test, expect } from '@playwright/test';
import { createDatabase } from '../support/database';

const apiURL=process.env.API_URL??'http://127.0.0.1:8080/api';
const database=createDatabase();
test.afterAll(async()=>{await database.end();});

test('@data seed contains exactly 10000 unique fictional employees',async({request})=>{
 const api=await request.get(`${apiURL}/employees`,{params:{page:0,size:1,q:'ACME-'}});expect(api.ok()).toBeTruthy();expect((await api.json()).data.totalItems).toBe(10000);
 const count=await database.query("SELECT count(*)::int AS count FROM employees WHERE employee_number LIKE 'ACME-%'");expect(count.rows[0].count).toBe(10000);
 const duplicates=await database.query("SELECT count(*)::int AS count FROM (SELECT lower(work_email) FROM employees WHERE employee_number LIKE 'ACME-%' GROUP BY lower(work_email) HAVING count(*)>1) x");expect(duplicates.rows[0].count).toBe(0);
});

test('@data seed salary and reference data satisfy database rules',async()=>{
 const invalid=await database.query(`SELECT count(*)::int AS count FROM employees e
  JOIN salary_versions s ON s.employee_id=e.employee_id
  LEFT JOIN countries c ON c.country_code=e.country_code
  LEFT JOIN departments d ON d.department_id=e.department_id
  WHERE e.employee_number LIKE 'ACME-%' AND (c.country_code IS NULL OR d.department_id IS NULL OR
   s.currency_code<>c.currency_code OR s.basic_amount IS NULL OR s.allowances_amount IS NULL OR s.deductions_amount IS NULL OR
   s.basic_amount<0 OR s.allowances_amount<0 OR s.deductions_amount<0 OR s.gross<>s.basic_amount+s.allowances_amount OR
   s.net<>s.gross-s.deductions_amount OR s.net<0)`);
 expect(invalid.rows[0].count).toBe(0);
 const groups=await database.query(`SELECT count(*)::int AS groups,min(n)::int AS minimum,max(n)::int AS maximum FROM
  (SELECT e.job_title,e.country_code,s.currency_code,count(*) n FROM employees e JOIN salary_versions s USING(employee_id)
   WHERE e.employee_number LIKE 'ACME-%' GROUP BY e.job_title,e.country_code,s.currency_code) g`);
 expect(groups.rows[0]).toMatchObject({groups:50,minimum:200,maximum:200});
});

test('@data country insight aggregates match PostgreSQL directly',async({request})=>{
 const api=await request.get(`${apiURL}/insights/pay`,{params:{groupBy:'country'}});expect(api.ok()).toBeTruthy();
 const result=(await api.json()).data.groups;
 const direct=await database.query(`WITH current_salary AS (SELECT DISTINCT ON (employee_id) employee_id,gross,currency_code FROM salary_versions WHERE effective_date<=CURRENT_DATE ORDER BY employee_id,effective_date DESC)
  SELECT c.name AS group_name,s.currency_code,count(*)::int AS employee_count,round(avg(s.gross),2) AS average,percentile_cont(0.5) WITHIN GROUP (ORDER BY s.gross) AS median
  FROM employees e JOIN current_salary s USING(employee_id) JOIN countries c ON c.country_code=e.country_code WHERE e.archived_at IS NULL
  GROUP BY c.name,s.currency_code ORDER BY group_name,s.currency_code`);
 expect(result).toHaveLength(direct.rows.length);
 for(const row of direct.rows){const actual=result.find((r:{groupName:string;currencyCode:string})=>r.groupName===row.group_name&&r.currencyCode===row.currency_code);expect(actual).toBeTruthy();expect(actual.employeeCount).toBe(row.employee_count);expect(actual.average).toBe(Number(row.average));expect(actual.median).toBe(Number(row.median));}
});

test('@data every seeded outlier group has at least five salaries',async({request})=>{
 const result=await request.get(`${apiURL}/insights/outliers`);expect(result.ok()).toBeTruthy();
 const rows=(await result.json()).data.filter((row:{employeeNumber:string})=>row.employeeNumber.startsWith('ACME-'));expect(rows).toHaveLength(50);
 expect(rows.every((row:{employeeNumber:string;gross:number;upperBound:number})=>row.gross>row.upperBound)).toBeTruthy();
});
