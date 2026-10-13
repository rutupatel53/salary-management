package com.acme.salary.service;

import com.acme.salary.dto.PayInsightsResponse;
import com.acme.salary.dto.PaySummaryRow;
import com.acme.salary.dto.SalaryOutlierResponse;
import com.acme.salary.enums.InsightDimension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InsightsServiceImpl implements InsightsService {
    private static final String CURRENT_SALARY_CTE = """
            WITH current_salary AS (
                SELECT DISTINCT ON (employee_id)
                       employee_id, gross, currency_code
                FROM salary_versions
                WHERE effective_date <= CURRENT_DATE
                ORDER BY employee_id, effective_date DESC
            )
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public InsightsServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PayInsightsResponse pay(String groupBy, String country, UUID departmentId, String jobTitle) {
        InsightDimension dimension = InsightDimension.parse(groupBy);
        String groupExpression = dimension.expression();
        String sql = CURRENT_SALARY_CTE + """
                SELECT %s AS group_name,
                       salary.currency_code,
                       count(*) AS employee_count,
                       avg(salary.gross) AS average,
                       percentile_cont(0.5) WITHIN GROUP (ORDER BY salary.gross) AS median,
                       min(salary.gross) AS minimum,
                       max(salary.gross) AS maximum
                FROM employees employee
                JOIN current_salary salary USING (employee_id)
                JOIN countries country_ref ON country_ref.country_code = employee.country_code
                JOIN departments department_ref ON department_ref.department_id = employee.department_id
                WHERE employee.archived_at IS NULL%s
                GROUP BY %s, salary.currency_code
                ORDER BY group_name, salary.currency_code
                """.formatted(groupExpression, employeeFilters(country, departmentId, jobTitle), groupExpression);

        List<PaySummaryRow> summaries = jdbcTemplate.query(sql, parameters(country, departmentId, jobTitle),
                this::mapSummary);
        return new PayInsightsResponse(groupBy, summaries);
    }

    @Override
    public List<SalaryOutlierResponse> outliers(String country, UUID departmentId, String jobTitle) {
        String boundsFilters = employeeFilters(country, null, jobTitle);
        String departmentFilter = departmentId == null ? "" : " AND employee.department_id = :department";
        String sql = CURRENT_SALARY_CTE + """
                , bounds AS (
                    SELECT employee.job_title,
                           employee.country_code,
                           salary.currency_code,
                           percentile_cont(0.25) WITHIN GROUP (ORDER BY salary.gross) AS q1,
                           percentile_cont(0.75) WITHIN GROUP (ORDER BY salary.gross) AS q3
                    FROM employees employee
                    JOIN current_salary salary USING (employee_id)
                    WHERE employee.archived_at IS NULL%s
                    GROUP BY employee.job_title, employee.country_code, salary.currency_code
                    HAVING count(*) >= 5
                )
                SELECT employee.employee_id,
                       employee.employee_number,
                       employee.first_name,
                       employee.last_name,
                       employee.job_title,
                       country_ref.name AS country_name,
                       salary.currency_code,
                       salary.gross,
                       bounds.q1 - 1.5 * (bounds.q3 - bounds.q1) AS lower_bound,
                       bounds.q3 + 1.5 * (bounds.q3 - bounds.q1) AS upper_bound
                FROM employees employee
                JOIN current_salary salary USING (employee_id)
                JOIN countries country_ref ON country_ref.country_code = employee.country_code
                JOIN bounds ON bounds.job_title = employee.job_title
                           AND bounds.country_code = employee.country_code
                           AND bounds.currency_code = salary.currency_code
                WHERE employee.archived_at IS NULL
                  AND (salary.gross < bounds.q1 - 1.5 * (bounds.q3 - bounds.q1)
                       OR salary.gross > bounds.q3 + 1.5 * (bounds.q3 - bounds.q1))%s
                ORDER BY employee.job_title, country_ref.name, salary.currency_code, employee.employee_number
                """.formatted(boundsFilters, departmentFilter);

        return jdbcTemplate.query(sql, parameters(country, departmentId, jobTitle), (resultSet, rowNumber) ->
                new SalaryOutlierResponse(
                        resultSet.getObject("employee_id", UUID.class),
                        resultSet.getString("employee_number"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("job_title"),
                        resultSet.getString("country_name"),
                        resultSet.getString("currency_code"),
                        resultSet.getBigDecimal("gross"),
                        resultSet.getBigDecimal("lower_bound"),
                        resultSet.getBigDecimal("upper_bound")));
    }

    private String employeeFilters(String country, UUID departmentId, String jobTitle) {
        StringBuilder filters = new StringBuilder();
        if (country != null) {
            filters.append(" AND employee.country_code = :country");
        }
        if (departmentId != null) {
            filters.append(" AND employee.department_id = :department");
        }
        if (jobTitle != null && !jobTitle.isBlank()) {
            filters.append(" AND lower(employee.job_title) = lower(:jobTitle)");
        }
        return filters.toString();
    }

    private MapSqlParameterSource parameters(String country, UUID departmentId, String jobTitle) {
        return new MapSqlParameterSource()
                .addValue("country", country)
                .addValue("department", departmentId)
                .addValue("jobTitle", jobTitle);
    }

    private PaySummaryRow mapSummary(ResultSet resultSet, int rowNumber) throws SQLException {
        BigDecimal minimum = resultSet.getBigDecimal("minimum");
        BigDecimal maximum = resultSet.getBigDecimal("maximum");
        return new PaySummaryRow(
                resultSet.getString("group_name"),
                resultSet.getString("currency_code"),
                resultSet.getLong("employee_count"),
                resultSet.getBigDecimal("average").setScale(2, RoundingMode.HALF_UP),
                resultSet.getBigDecimal("median").setScale(2, RoundingMode.HALF_UP),
                minimum,
                maximum,
                maximum.subtract(minimum));
    }
}
