package com.acme.salary.seed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public record SeedEmployee(UUID id,String employeeNumber,String firstName,String lastName,String workEmail,
 LocalDate hireDate,String countryCode,UUID departmentId,String jobTitle,String currency,
 BigDecimal basic,BigDecimal allowances,BigDecimal deductions,UUID salaryVersionId,LocalDate effectiveDate) { }
