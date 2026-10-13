package com.acme.salary.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record SalaryOutlierResponse(UUID employeeId,String employeeNumber,String firstName,String lastName,String jobTitle,String countryName,String currencyCode,BigDecimal gross,BigDecimal lowerBound,BigDecimal upperBound){}
