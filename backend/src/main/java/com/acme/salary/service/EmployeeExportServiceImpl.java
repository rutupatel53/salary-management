package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.EmployeeRepository;
import com.acme.salary.specification.EmployeeSpecifications;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeExportServiceImpl implements EmployeeExportService {
    private static final int EXPORT_PAGE_SIZE = 500;
    private static final String[] HEADERS = {
            "Employee Number", "First Name", "Last Name", "Work Email", "Hire Date", "Country Code",
            "Department", "Job Title", "Currency", "Basic", "Allowances", "Deductions", "Gross", "Net",
            "Salary Effective Date"
    };

    private static final String LATEST_SALARY_SQL = """
            SELECT DISTINCT ON (employee_id)
                   employee_id, currency_code, basic_amount, allowances_amount, deductions_amount,
                   gross, net, effective_date
            FROM salary_versions
            WHERE employee_id IN (%s) AND effective_date <= CURRENT_DATE
            ORDER BY employee_id, effective_date DESC
            """;

    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AuditService auditService;

    public EmployeeExportServiceImpl(
            EmployeeRepository employeeRepository, JdbcTemplate jdbcTemplate, AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public byte[] export(String query, String country, UUID departmentId, String jobTitle) {
        var specification = EmployeeSpecifications.active().and(
                EmployeeSpecifications.filters(query, country, departmentId, jobTitle));
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet("Employees");
            writeHeaders(sheet.createRow(0));

            int excelRowNumber = 1;
            for (int pageNumber = 0; ; pageNumber++) {
                var page = employeeRepository.findAll(specification, PageRequest.of(
                        pageNumber, EXPORT_PAGE_SIZE,
                        Sort.by("employeeNumber").ascending().and(Sort.by("id").ascending())));
                if (page.isEmpty()) {
                    break;
                }

                Map<UUID, SalaryExport> salaries = latestSalaries(
                        page.getContent().stream().map(Employee::getId).toList());
                for (Employee employee : page.getContent()) {
                    writeEmployee(sheet.createRow(excelRowNumber++), employee, salaries.get(employee.getId()));
                }
                if (!page.hasNext()) {
                    break;
                }
            }

            workbook.write(output);
            auditService.record("EXPORT_CREATED", null, Map.of("rowCount", excelRowNumber - 1));
            return output.toByteArray();
        } catch (Exception exception) {
            if (exception instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Could not create the employee export.", exception);
        }
    }

    private void writeHeaders(Row row) {
        for (int column = 0; column < HEADERS.length; column++) {
            row.createCell(column).setCellValue(HEADERS[column]);
        }
    }

    private void writeEmployee(Row row, Employee employee, SalaryExport salary) {
        row.createCell(0).setCellValue(employee.getEmployeeNumber());
        row.createCell(1).setCellValue(employee.getFirstName());
        row.createCell(2).setCellValue(employee.getLastName());
        row.createCell(3).setCellValue(employee.getWorkEmail());
        row.createCell(4).setCellValue(employee.getHireDate().toString());
        row.createCell(5).setCellValue(employee.getCountry().getCode());
        row.createCell(6).setCellValue(employee.getDepartment().getName());
        row.createCell(7).setCellValue(employee.getJobTitle());
        if (salary == null) {
            return;
        }

        row.createCell(8).setCellValue(salary.currency());
        row.createCell(9).setCellValue(salary.basic().toPlainString());
        row.createCell(10).setCellValue(salary.allowances().toPlainString());
        row.createCell(11).setCellValue(salary.deductions().toPlainString());
        row.createCell(12).setCellValue(salary.gross().toPlainString());
        row.createCell(13).setCellValue(salary.net().toPlainString());
        row.createCell(14).setCellValue(salary.effectiveDate().toString());
    }

    private Map<UUID, SalaryExport> latestSalaries(List<UUID> employeeIds) {
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

    private record SalaryExport(
            String currency,
            BigDecimal basic,
            BigDecimal allowances,
            BigDecimal deductions,
            BigDecimal gross,
            BigDecimal net,
            LocalDate effectiveDate) { }
}
