package com.acme.salary.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record SalaryChangeRequest(@NotNull @DecimalMin("0.00") @DecimalMax("999999999999.99") BigDecimal basic,
 @NotNull @DecimalMin("0.00") @DecimalMax("999999999999.99") BigDecimal allowances,
 @NotNull @DecimalMin("0.00") @DecimalMax("999999999999.99") BigDecimal deductions,
 @NotBlank @Pattern(regexp="USD|INR|GBP|EUR|CAD") String currencyCode,
 @NotNull LocalDate effectiveDate){}
