package com.acme.salary.seed;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name="app.seed.enabled",havingValue="true")
public class SeedDataRunner implements ApplicationRunner {
 private final JdbcTemplate jdbc;private final SeedDataGenerator generator;
 public SeedDataRunner(JdbcTemplate jdbc,SeedDataGenerator generator){this.jdbc=jdbc;this.generator=generator;}
 @Override @Transactional
 public void run(ApplicationArguments args){
  var employees=generator.generate();
  jdbc.batchUpdate("INSERT INTO employees(employee_id,employee_number,first_name,last_name,work_email,hire_date,country_code,department_id,job_title) VALUES (?,?,?,?,?,?,?,?,?) ON CONFLICT(employee_id) DO NOTHING",
   employees,500,(ps,e)->{ps.setObject(1,e.id());ps.setString(2,e.employeeNumber());ps.setString(3,e.firstName());ps.setString(4,e.lastName());ps.setString(5,e.workEmail());ps.setObject(6,e.hireDate());ps.setString(7,e.countryCode());ps.setObject(8,e.departmentId());ps.setString(9,e.jobTitle());});
  jdbc.batchUpdate("INSERT INTO salary_versions(salary_version_id,employee_id,effective_date,currency_code,basic_amount,allowances_amount,deductions_amount) VALUES (?,?,?,?,?,?,?) ON CONFLICT(salary_version_id) DO NOTHING",
   employees,500,(ps,e)->{ps.setObject(1,e.salaryVersionId());ps.setObject(2,e.id());ps.setObject(3,e.effectiveDate());ps.setString(4,e.currency());ps.setBigDecimal(5,e.basic());ps.setBigDecimal(6,e.allowances());ps.setBigDecimal(7,e.deductions());});
 }
}
