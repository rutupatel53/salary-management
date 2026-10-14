package com.acme.salary.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SalaryAssignmentRequest(
        @NotNull UUID employeeId,
        @NotNull UUID templateId,
        @NotNull Long templateRevision,
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal annualCtc,
        @NotNull LocalDate effectiveDate) {
}
