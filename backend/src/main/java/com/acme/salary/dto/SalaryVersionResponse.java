package com.acme.salary.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public record SalaryVersionResponse(UUID id,LocalDate effectiveDate,String currencyCode,BigDecimal basic,
 BigDecimal allowances,BigDecimal deductions,BigDecimal gross,BigDecimal net){}
