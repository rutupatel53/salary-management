package com.acme.salary.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeRequest(
 @NotBlank @Pattern(regexp="[A-Za-z0-9-]{2,24}") String employeeNumber,
 @NotBlank @Size(max=80) String firstName,
 @NotBlank @Size(max=80) String lastName,
 @NotBlank @Email @Size(max=254) String workEmail,
 @NotNull @PastOrPresent LocalDate hireDate,
 @NotBlank @Pattern(regexp="US|IN|GB|DE|CA") String countryCode,
 @NotNull UUID departmentId,
 @NotBlank @Size(max=100) String jobTitle) { }
