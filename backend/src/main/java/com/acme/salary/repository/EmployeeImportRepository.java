package com.acme.salary.repository;

import com.acme.salary.dto.PreparedImportRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class EmployeeImportRepository {
    private static final int BATCH_SIZE = 100;
    private final JdbcTemplate jdbcTemplate;

    public EmployeeImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertEmployees(List<PreparedImportRow> rows) {
        String sql = """
                INSERT INTO employees (
                    employee_id, employee_number, first_name, last_name, work_email, hire_date,
                    country_code, department_id, job_title)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.batchUpdate(sql, rows, BATCH_SIZE, (statement, row) -> {
            statement.setObject(1, row.employeeId());
            statement.setString(2, row.employee().employeeNumber());
            statement.setString(3, row.employee().firstName());
            statement.setString(4, row.employee().lastName());
            statement.setString(5, row.employee().workEmail());
            statement.setObject(6, row.employee().hireDate());
            statement.setString(7, row.employee().countryCode());
            statement.setObject(8, row.employee().departmentId());
            statement.setString(9, row.employee().jobTitle());
        });
    }

    public void insertSalaryVersions(List<PreparedImportRow> rows) {
        String sql = """
                INSERT INTO salary_versions (
                    salary_version_id, employee_id, effective_date, currency_code,
                    basic_amount, allowances_amount, deductions_amount)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.batchUpdate(sql, rows, BATCH_SIZE, (statement, row) -> {
            statement.setObject(1, row.salaryVersionId());
            statement.setObject(2, row.employeeId());
            statement.setObject(3, row.salary().effectiveDate());
            statement.setString(4, row.salary().currencyCode());
            statement.setBigDecimal(5, row.salary().basic());
            statement.setBigDecimal(6, row.salary().allowances());
            statement.setBigDecimal(7, row.salary().deductions());
        });
    }

}
