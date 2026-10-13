package com.acme.salary.dto;
import java.math.BigDecimal;
public record PaySummaryRow(String groupName,String currencyCode,long employeeCount,BigDecimal average,BigDecimal median,BigDecimal minimum,BigDecimal maximum,BigDecimal range){}
