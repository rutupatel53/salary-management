package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.exception.ImportValidationException;
import com.acme.salary.model.Country;
import com.acme.salary.model.Department;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import com.acme.salary.repository.EmployeeRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExcelImportServiceImplTest {
    private EmployeeRepository employees;
    private CountryRepository countries;
    private DepartmentRepository departments;
    private JdbcTemplate jdbc;
    private AuditService audit;
    private Validator validator;
    private ValidatorFactory validatorFactory;
    private ExcelImportServiceImpl service;

    @BeforeEach void setUp() {
        employees = mock(EmployeeRepository.class); countries = mock(CountryRepository.class);
        departments = mock(DepartmentRepository.class); jdbc = mock(JdbcTemplate.class); audit = mock(AuditService.class);
        validatorFactory = Validation.buildDefaultValidatorFactory(); validator = validatorFactory.getValidator();
        service = new ExcelImportServiceImpl(employees, countries, departments, validator, jdbc, audit);
        Department department = mock(Department.class); when(department.getId()).thenReturn(UUID.fromString("00000000-0000-5000-8000-000000000001"));
        when(department.getName()).thenReturn("Engineering");
        when(departments.findByActiveTrueOrderByName()).thenReturn(List.of(department));
        Country country = mock(Country.class); when(country.getCurrencyCode()).thenReturn("INR");
        when(country.getCode()).thenReturn("IN");
        when(countries.findAll()).thenReturn(List.of(country));
        when(employees.findExistingEmployeeNumbers(anyCollection())).thenReturn(Set.of());
        when(employees.findExistingWorkEmails(anyCollection())).thenReturn(Set.of());
    }

    @AfterEach void closeValidator() { validatorFactory.close(); }

    @Test void previewReturnsValidRowsWithoutWriting() throws Exception {
        var response = service.preview(workbook("IMP-101", "rutu.import@example.test", "85000", "5000", "10000"));
        assertEquals(1, response.totalRows()); assertEquals(1, response.validRows()); assertEquals(0, response.invalidRows());
        verifyNoInteractions(jdbc, audit);
    }

    @Test void previewReportsNegativeNetAtRowLevel() throws Exception {
        var response = service.preview(workbook("IMP-102", "negative@example.test", "10000", "0", "10001"));
        assertEquals(1, response.invalidRows());
        assertTrue(response.rows().get(0).errors().stream().anyMatch(message -> message.contains("Deductions cannot exceed gross")));
        verifyNoInteractions(jdbc, audit);
    }

    @Test void previewRejectsMoneyWithMoreThanTwoDecimalPlaces() throws Exception {
        var response = service.preview(workbook("IMP-106", "scale@example.test", "85000.001", "5000", "10000"));

        assertEquals(1, response.invalidRows());
        assertTrue(response.rows().get(0).errors().stream()
                .anyMatch(message -> message.contains("Basic must be non-negative with at most two decimal places.")));
        verifyNoInteractions(jdbc, audit);
    }

    @Test void confirmRevalidatesAndWritesEmployeeSalaryAndAuditTogether() throws Exception {
        var result = service.confirm(workbook("IMP-103", "confirm@example.test", "85000", "5000", "10000"));
        assertEquals(1, result.createdRows());
        verify(jdbc, times(2)).batchUpdate(anyString(), anyList(), anyInt(), any());
        verify(audit).record(eq("IMPORT_CONFIRMED"), isNull(), anyMap());
    }

    @Test void confirmRejectsInvalidWorkbookBeforeAnyWrites() throws Exception {
        var file = workbook("IMP-104", "bad@example.test", "10000", "0", "20000");
        assertThrows(ImportValidationException.class, () -> service.confirm(file));
        verifyNoInteractions(jdbc, audit);
    }

    private MockMultipartFile workbook(String number, String email, String basic, String allowances, String deductions) throws Exception {
        try (var workbook = new XSSFWorkbook(); var bytes = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Employees");
            Row header = sheet.createRow(0);
            for (int i = 0; i < ExcelImportServiceImpl.HEADERS.size(); i++) header.createCell(i).setCellValue(ExcelImportServiceImpl.HEADERS.get(i));
            Row row = sheet.createRow(1);
            String[] values = {number, "Rutu", "Patel", email, "2024-01-15", "IN", "Engineering", "Assessment Engineer", "INR", basic, allowances, deductions, LocalDate.now().minusDays(1).toString()};
            for (int i = 0; i < values.length; i++) row.createCell(i).setCellValue(values[i]);
            workbook.write(bytes);
            return new MockMultipartFile("file", "import.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bytes.toByteArray());
        }
    }
}
