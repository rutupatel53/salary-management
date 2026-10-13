package com.acme.salary.dto;
import java.time.LocalDate;
import java.util.UUID;
public record EmployeeResponse(UUID id, String employeeNumber, String firstName, String lastName,
 String workEmail, LocalDate hireDate, String countryCode, String countryName, UUID departmentId,
 String departmentName, String jobTitle, String currencyCode) { }
