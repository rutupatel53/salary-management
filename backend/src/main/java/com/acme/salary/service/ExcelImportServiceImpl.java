package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.EmployeeRequest;
import com.acme.salary.dto.ImportConfirmResponse;
import com.acme.salary.dto.ImportPreviewResponse;
import com.acme.salary.dto.ImportRowResult;
import com.acme.salary.dto.SalaryChangeRequest;
import com.acme.salary.exception.ImportValidationException;
import com.acme.salary.model.Country;
import com.acme.salary.model.Department;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import com.acme.salary.repository.EmployeeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.acme.salary.repository.EmployeeImportRepository;
import com.acme.salary.dto.PreparedImportRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {
    public static final List<String> HEADERS = List.of(
            "Employee Number", "First Name", "Last Name", "Work Email", "Hire Date", "Country Code",
            "Department", "Job Title", "Currency", "Basic", "Allowances", "Deductions",
            "Salary Effective Date");

    private static final int MAX_BYTES = 5 * 1024 * 1024;
    private static final int MAX_ROWS = 500;
    private static final Pattern EMPLOYEE_NUMBER = Pattern.compile("[A-Za-z0-9-]{2,24}");

    private final EmployeeRepository employeeRepository;
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;
    private final Validator validator;
    private final EmployeeImportRepository importRepository;
    private final AuditService auditService;

    public ExcelImportServiceImpl(
            EmployeeRepository employeeRepository,
            CountryRepository countryRepository,
            DepartmentRepository departmentRepository,
            Validator validator,
            EmployeeImportRepository importRepository,
            AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
        this.validator = validator;
        this.importRepository = importRepository;
        this.auditService = auditService;
    }

    @Override
    public byte[] template() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employees");
            Row header = sheet.createRow(0);
            for (int column = 0; column < HEADERS.size(); column++) {
                header.createCell(column, CellType.STRING).setCellValue(HEADERS.get(column));
                int width = Math.min(HEADERS.get(column).length() + 5, 32) * 256;
                sheet.setColumnWidth(column, width);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create the Excel template.", exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ImportPreviewResponse preview(MultipartFile file) {
        return validate(file).response();
    }

    @Override
    @Transactional
    public ImportConfirmResponse confirm(MultipartFile file) {
        Validation validation = validate(file);
        ImportPreviewResponse preview = validation.response();
        if (preview.totalRows() == 0 || preview.invalidRows() > 0 || !preview.fileErrors().isEmpty()) {
            throw new ImportValidationException(preview);
        }

        List<PreparedImportRow> rows = validation.preparedRows();
        importRepository.insertEmployees(rows);
        importRepository.insertSalaryVersions(rows);
        auditService.record("IMPORT_CONFIRMED", null, Map.of("rowCount", rows.size()));
        return new ImportConfirmResponse(rows.size(), "Import completed.");
    }

    private Validation validate(MultipartFile file) {
        List<String> fileErrors = validateFile(file);
        if (!fileErrors.isEmpty()) {
            return invalidFile(fileErrors);
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                return invalidFile(List.of("Workbook must have an Employees worksheet."));
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0) {
                return invalidFile(List.of("Workbook is empty."));
            }

            DataFormatter formatter = new DataFormatter(Locale.US);
            List<String> headerErrors = validateHeaders(sheet.getRow(0), formatter);
            if (!headerErrors.isEmpty()) {
                return invalidFile(headerErrors);
            }
            if (sheet.getLastRowNum() > MAX_ROWS) {
                return invalidFile(List.of("Workbook can contain at most " + MAX_ROWS + " employee rows."));
            }

            return validateRows(sheet, formatter);
        } catch (IOException | IllegalArgumentException exception) {
            return invalidFile(List.of("Could not read this workbook. Download a fresh .xlsx template and try again."));
        }
    }

    private List<String> validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return List.of("Choose an .xlsx workbook.");
        }
        List<String> errors = new ArrayList<>();
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        if (!filename.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            errors.add("Only .xlsx workbooks are supported.");
        }
        if (file.getSize() > MAX_BYTES) {
            errors.add("Workbook must be 5 MB or smaller.");
        }
        return errors;
    }

    private List<String> validateHeaders(Row header, DataFormatter formatter) {
        List<String> errors = new ArrayList<>();
        for (int column = 0; column < HEADERS.size(); column++) {
            String value = cellText(header == null ? null : header.getCell(column), formatter).trim();
            if (!HEADERS.get(column).equalsIgnoreCase(value)) {
                errors.add("Column " + (column + 1) + " must be '" + HEADERS.get(column) + "'.");
            }
        }
        if (header != null && header.getLastCellNum() > HEADERS.size()) {
            errors.add("Workbook has unexpected columns after 'Salary Effective Date'.");
        }
        return errors;
    }

    private Validation validateRows(Sheet sheet, DataFormatter formatter) {
        Map<String, Department> activeDepartments = departmentRepository.findByActiveTrueOrderByName().stream()
                .collect(Collectors.toMap(department -> normalize(department.getName()), department -> department));
        Map<String, Country> countries = countryRepository.findAll().stream()
                .collect(Collectors.toMap(Country::getCode, country -> country));
        IdentityCandidates candidates = collectIdentityCandidates(sheet, formatter);
        Set<String> existingEmployeeNumbers = candidates.employeeNumbers().isEmpty()
                ? Set.of() : employeeRepository.findExistingEmployeeNumbers(candidates.employeeNumbers());
        Set<String> existingWorkEmails = candidates.workEmails().isEmpty()
                ? Set.of() : employeeRepository.findExistingWorkEmails(candidates.workEmails());

        List<PreparedImportRow> preparedRows = new ArrayList<>();
        List<ImportRowResult> results = new ArrayList<>();
        Map<String, Integer> seenNumbers = new HashMap<>();
        Map<String, Integer> seenEmails = new HashMap<>();

        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            Row row = sheet.getRow(index);
            if (isBlank(row, formatter)) {
                continue;
            }
            validateRow(row, index + 1, formatter, activeDepartments, countries,
                    existingEmployeeNumbers, existingWorkEmails, seenNumbers, seenEmails,
                    preparedRows, results);
        }

        List<String> fileErrors = results.isEmpty() ? List.of("Workbook must contain at least one employee row.") : List.of();
        int validRows = (int) results.stream().filter(ImportRowResult::valid).count();
        int invalidRows = results.size() - validRows;
        ImportPreviewResponse response = new ImportPreviewResponse(
                results.size(), validRows, invalidRows, fileErrors, List.copyOf(results));
        return new Validation(response, List.copyOf(preparedRows));
    }

    private void validateRow(
            Row row,
            int rowNumber,
            DataFormatter formatter,
            Map<String, Department> activeDepartments,
            Map<String, Country> countries,
            Set<String> existingEmployeeNumbers,
            Set<String> existingWorkEmails,
            Map<String, Integer> seenNumbers,
            Map<String, Integer> seenEmails,
            List<PreparedImportRow> preparedRows,
            List<ImportRowResult> results) {
        List<String> errors = new ArrayList<>();
        String employeeNumber = cellText(row.getCell(0), formatter).trim();
        String firstName = cellText(row.getCell(1), formatter).trim();
        String lastName = cellText(row.getCell(2), formatter).trim();
        String email = cellText(row.getCell(3), formatter).trim().toLowerCase(Locale.ROOT);
        LocalDate hireDate = parseDate(row.getCell(4), formatter, "Hire date", errors);
        String countryCode = cellText(row.getCell(5), formatter).trim().toUpperCase(Locale.ROOT);
        String departmentName = cellText(row.getCell(6), formatter).trim();
        String jobTitle = cellText(row.getCell(7), formatter).trim();
        String currency = cellText(row.getCell(8), formatter).trim().toUpperCase(Locale.ROOT);
        BigDecimal basic = parseMoney(row.getCell(9), formatter, "Basic", errors);
        BigDecimal allowances = parseMoney(row.getCell(10), formatter, "Allowances", errors);
        BigDecimal deductions = parseMoney(row.getCell(11), formatter, "Deductions", errors);
        LocalDate effectiveDate = parseDate(row.getCell(12), formatter, "Salary effective date", errors);

        if (!EMPLOYEE_NUMBER.matcher(employeeNumber).matches()) {
            errors.add("Employee number must be 2–24 letters, digits, or hyphens.");
        }
        Department department = activeDepartments.get(normalize(departmentName));
        if (department == null) {
            errors.add("Department must match an active department.");
        }

        UUID departmentId = department == null ? null : department.getId();
        EmployeeRequest employee = new EmployeeRequest(
                employeeNumber, firstName, lastName, email, hireDate, countryCode, departmentId, jobTitle);
        validator.validate(employee).forEach(violation -> errors.add(validationMessage(violation)));

        Country country = countries.get(countryCode);
        if (country == null) {
            errors.add("Country code is not supported.");
        } else if (!country.getCurrencyCode().equals(currency)) {
            errors.add("Currency must match the country's local currency.");
        }
        if (basic != null && allowances != null && deductions != null
                && basic.add(allowances).subtract(deductions).signum() < 0) {
            errors.add("Deductions cannot exceed gross pay.");
        }
        if (existingEmployeeNumbers.contains(normalize(employeeNumber))) {
            errors.add("Employee number already exists.");
        }
        if (!email.isBlank() && existingWorkEmails.contains(email)) {
            errors.add("Work email already exists.");
        }
        addDuplicateError(seenNumbers, normalize(employeeNumber), rowNumber, "Employee number", errors);
        addDuplicateError(seenEmails, email, rowNumber, "Work email", errors);

        if (errors.isEmpty()) {
            SalaryChangeRequest salary = new SalaryChangeRequest(
                    money(basic), money(allowances), money(deductions), currency, effectiveDate);
            validator.validate(salary).forEach(violation -> errors.add(validationMessage(violation)));
            if (errors.isEmpty()) {
                preparedRows.add(new PreparedImportRow(UUID.randomUUID(), UUID.randomUUID(), employee, salary));
            }
        }
        results.add(new ImportRowResult(rowNumber, employeeNumber, errors.isEmpty(), List.copyOf(errors)));
    }

    private void addDuplicateError(
            Map<String, Integer> seenValues, String value, int rowNumber, String label, List<String> errors) {
        if (value.isBlank()) {
            return;
        }
        Integer previousRow = seenValues.putIfAbsent(value, rowNumber);
        if (previousRow != null) {
            errors.add(label + " duplicates row " + previousRow + ".");
        }
    }

    private IdentityCandidates collectIdentityCandidates(Sheet sheet, DataFormatter formatter) {
        Set<String> employeeNumbers = new HashSet<>();
        Set<String> workEmails = new HashSet<>();
        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            Row row = sheet.getRow(index);
            if (isBlank(row, formatter)) {
                continue;
            }
            String number = cellText(row.getCell(0), formatter).trim();
            String email = cellText(row.getCell(3), formatter).trim();
            if (!number.isBlank()) {
                employeeNumbers.add(normalize(number));
            }
            if (!email.isBlank()) {
                workEmails.add(normalize(email));
            }
        }
        return new IdentityCandidates(employeeNumbers, workEmails);
    }

    private LocalDate parseDate(Cell cell, DataFormatter formatter, String label, List<String> errors) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errors.add(label + " is required.");
            return null;
        }
        try {
            if (cell.getCellType() == CellType.FORMULA) {
                throw new IllegalArgumentException();
            }
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }
            return LocalDate.parse(formatter.formatCellValue(cell).trim());
        } catch (RuntimeException exception) {
            errors.add(label + " must be an Excel date or YYYY-MM-DD.");
            return null;
        }
    }

    private BigDecimal parseMoney(Cell cell, DataFormatter formatter, String label, List<String> errors) {
        String value = cellText(cell, formatter).trim();
        try {
            if (value.isEmpty() || cell != null && cell.getCellType() == CellType.FORMULA) {
                throw new NumberFormatException();
            }
            BigDecimal amount = new BigDecimal(value);
            if (amount.signum() < 0 || amount.scale() > 2) {
                throw new NumberFormatException();
            }
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (NumberFormatException | ArithmeticException exception) {
            errors.add(label + " must be non-negative with at most two decimal places.");
            return null;
        }
    }

    private String cellText(Cell cell, DataFormatter formatter) {
        if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.FORMULA) {
            return "";
        }
        return formatter.formatCellValue(cell);
    }

    private boolean isBlank(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        for (int column = 0; column < HEADERS.size(); column++) {
            if (!cellText(row.getCell(column), formatter).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String validationMessage(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    private Validation invalidFile(List<String> errors) {
        ImportPreviewResponse response = new ImportPreviewResponse(0, 0, 0, List.copyOf(errors), List.of());
        return new Validation(response, List.of());
    }

    private record Validation(ImportPreviewResponse response, List<PreparedImportRow> preparedRows) { }

    private record IdentityCandidates(Set<String> employeeNumbers, Set<String> workEmails) { }
}
