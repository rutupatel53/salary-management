package com.acme.salary.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class SalaryExportRepository {
    private static final String LATEST_SALARY_SQL = """
            SELECT DISTINCT ON (employee_id)
                   employee_id, currency_code, basic_amount, allowances_amount, deductions_amount,
                   gross, net, effective_date
            FROM salary_versions
            WHERE employee_id IN (%s) AND effective_date <= CURRENT_DATE
            ORDER BY employee_id, effective_date DESC
            """;

    private final JdbcTemplate jdbcTemplate;

    public SalaryExportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<UUID, SalaryExport> findCurrentByEmployeeIds(List<UUID> employeeIds) {
        if (employeeIds.isEmpty()) {
            return Map.of();
        }
        String placeholders = employeeIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = LATEST_SALARY_SQL.formatted(placeholders);
        return jdbcTemplate.query(sql, resultSet -> {
            Map<UUID, SalaryExport> salaries = new HashMap<>();
            while (resultSet.next()) {
                UUID employeeId = resultSet.getObject("employee_id", UUID.class);
                salaries.put(employeeId, new SalaryExport(
                        resultSet.getString("currency_code"),
                        resultSet.getBigDecimal("basic_amount"),
                        resultSet.getBigDecimal("allowances_amount"),
                        resultSet.getBigDecimal("deductions_amount"),
                        resultSet.getBigDecimal("gross"),
                        resultSet.getBigDecimal("net"),
                        resultSet.getDate("effective_date").toLocalDate()));
            }
            return salaries;
        }, employeeIds.toArray());
    }

    public record SalaryExport(
            String currency,
            BigDecimal basic,
            BigDecimal allowances,
            BigDecimal deductions,
            BigDecimal gross,
            BigDecimal net,
            LocalDate effectiveDate) { }
}
