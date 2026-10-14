package com.acme.salary.dto;

public record SalaryAssignmentPreview(
        EmployeeResponse employee,
        SalaryTemplateResponse template,
        SalaryVersionResponse previousSalary,
        SalaryBreakdown breakdown) {
}
