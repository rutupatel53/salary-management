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
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeExportServiceImpl implements EmployeeExportService {
    private final EmployeeRepository employees;
    private final JdbcTemplate jdbc;
    private final AuditService audit;

    public EmployeeExportServiceImpl(EmployeeRepository employees, JdbcTemplate jdbc, AuditService audit) {
        this.employees = employees; this.jdbc = jdbc; this.audit = audit;
    }

    @Override
    @Transactional
    public byte[] export(String query, String country, UUID department, String jobTitle) {
        var specification = EmployeeSpecifications.active().and(EmployeeSpecifications.filters(query, country, department, jobTitle));
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet("Employees");
            String[] headers = {"Employee Number", "First Name", "Last Name", "Work Email", "Hire Date", "Country Code", "Department", "Job Title", "Currency", "Basic", "Allowances", "Deductions", "Gross", "Net", "Salary Effective Date"};
            Row header = sheet.createRow(0);
            for (int col = 0; col < headers.length; col++) header.createCell(col).setCellValue(headers[col]);
            int rowNumber = 1;
            for (int page = 0; ; page++) {
                var rows = employees.findAll(specification, PageRequest.of(page, 500, Sort.by("employeeNumber").ascending().and(Sort.by("id").ascending())));
                if (rows.isEmpty()) break;
                Map<UUID, SalaryExport> salaries = latestSalaries(rows.getContent().stream().map(Employee::getId).toList());
                for (Employee employee : rows.getContent()) {
                    SalaryExport salary = salaries.get(employee.getId());
                    Row row = sheet.createRow(rowNumber++);
                    row.createCell(0).setCellValue(employee.getEmployeeNumber()); row.createCell(1).setCellValue(employee.getFirstName());
                    row.createCell(2).setCellValue(employee.getLastName()); row.createCell(3).setCellValue(employee.getWorkEmail());
                    row.createCell(4).setCellValue(employee.getHireDate().toString()); row.createCell(5).setCellValue(employee.getCountry().getCode());
                    row.createCell(6).setCellValue(employee.getDepartment().getName()); row.createCell(7).setCellValue(employee.getJobTitle());
                    if (salary != null) {
                        row.createCell(8).setCellValue(salary.currency()); row.createCell(9).setCellValue(salary.basic().doubleValue());
                        row.createCell(10).setCellValue(salary.allowances().doubleValue()); row.createCell(11).setCellValue(salary.deductions().doubleValue());
                        row.createCell(12).setCellValue(salary.gross().doubleValue()); row.createCell(13).setCellValue(salary.net().doubleValue());
                        row.createCell(14).setCellValue(salary.effective().toString());
                    }
                }
                if (!rows.hasNext()) break;
            }
            workbook.write(output);
            audit.record("EXPORT_CREATED", null, Map.of("rowCount", rowNumber - 1));
            return output.toByteArray();
        } catch (Exception e) {
            if (e instanceof RuntimeException runtime) throw runtime;
            throw new IllegalStateException("Could not create the employee export.", e);
        }
    }

    private Map<UUID, SalaryExport> latestSalaries(List<UUID> employeeIds) {
        if (employeeIds.isEmpty()) return Map.of();
        String placeholders = employeeIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT DISTINCT ON (employee_id) employee_id,currency_code,basic_amount,allowances_amount,deductions_amount,gross,net,effective_date " +
                "FROM salary_versions WHERE employee_id IN (" + placeholders + ") AND effective_date<=CURRENT_DATE ORDER BY employee_id,effective_date DESC";
        return jdbc.query(sql, rs -> {
            Map<UUID, SalaryExport> result = new HashMap<>();
            while (rs.next()) result.put((UUID) rs.getObject("employee_id"), new SalaryExport(rs.getString("currency_code"), rs.getBigDecimal("basic_amount"),
                    rs.getBigDecimal("allowances_amount"), rs.getBigDecimal("deductions_amount"), rs.getBigDecimal("gross"), rs.getBigDecimal("net"), rs.getDate("effective_date").toLocalDate()));
            return result;
        }, employeeIds.toArray());
    }

    private record SalaryExport(String currency, java.math.BigDecimal basic, java.math.BigDecimal allowances,
                                java.math.BigDecimal deductions, java.math.BigDecimal gross, java.math.BigDecimal net, java.time.LocalDate effective) { }
}
