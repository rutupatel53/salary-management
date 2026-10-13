package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.model.Country;
import com.acme.salary.model.Department;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.EmployeeRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmployeeExportServiceImplTest {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test void exportKeepsMoneyAsExactDecimalTextAndUsesSharedPagedQuery() throws Exception {
        UUID id = UUID.fromString("10000000-0000-5000-8000-000000000001");
        var employees = mock(EmployeeRepository.class); var jdbc = mock(JdbcTemplate.class); var audit = mock(AuditService.class);
        var employee = mock(Employee.class); var country = mock(Country.class); var department = mock(Department.class);
        when(employee.getId()).thenReturn(id); when(employee.getEmployeeNumber()).thenReturn("EMP-001");
        when(employee.getFirstName()).thenReturn("Test"); when(employee.getLastName()).thenReturn("Person");
        when(employee.getWorkEmail()).thenReturn("test@example.test"); when(employee.getHireDate()).thenReturn(LocalDate.of(2020, 1, 1));
        when(employee.getCountry()).thenReturn(country); when(country.getCode()).thenReturn("US");
        when(employee.getDepartment()).thenReturn(department); when(department.getName()).thenReturn("Engineering");
        when(employee.getJobTitle()).thenReturn("Engineer");
        when(employees.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee), PageRequest.of(0, 500), 1));
        doAnswer(invocation -> {
            ResultSet rs = mock(ResultSet.class);
            when(rs.next()).thenReturn(true, false); when(rs.getObject("employee_id", UUID.class)).thenReturn(id);
            when(rs.getString("currency_code")).thenReturn("USD");
            when(rs.getBigDecimal("basic_amount")).thenReturn(new BigDecimal("100000000.01"));
            when(rs.getBigDecimal("allowances_amount")).thenReturn(new BigDecimal("0.02"));
            when(rs.getBigDecimal("deductions_amount")).thenReturn(new BigDecimal("0.01"));
            when(rs.getBigDecimal("gross")).thenReturn(new BigDecimal("100000000.03"));
            when(rs.getBigDecimal("net")).thenReturn(new BigDecimal("100000000.02"));
            when(rs.getDate("effective_date")).thenReturn(Date.valueOf("2025-01-01"));
            return ((ResultSetExtractor) invocation.getArgument(1)).extractData(rs);
        }).when(jdbc).query(anyString(), any(ResultSetExtractor.class), any(Object[].class));

        byte[] file = new EmployeeExportServiceImpl(employees, jdbc, audit).export("EMP-001", "US", null, null);
        try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(file))) {
            var row = workbook.getSheetAt(0).getRow(1);
            assertEquals("100000000.01", row.getCell(9).getStringCellValue());
            assertEquals("0.02", row.getCell(10).getStringCellValue());
            assertEquals("100000000.03", row.getCell(12).getStringCellValue());
            assertEquals("100000000.02", row.getCell(13).getStringCellValue());
        }
        verify(employees).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class));
        verify(audit).record(eq("EXPORT_CREATED"), isNull(), eq(Map.of("rowCount", 1)));
    }
}
