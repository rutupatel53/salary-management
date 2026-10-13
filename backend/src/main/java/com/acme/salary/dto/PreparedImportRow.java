package com.acme.salary.dto;

import java.util.UUID;

public record PreparedImportRow(
            UUID employeeId,
            UUID salaryVersionId,
            EmployeeRequest employee,
            SalaryChangeRequest salary) { }
