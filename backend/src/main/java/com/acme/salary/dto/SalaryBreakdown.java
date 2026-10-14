package com.acme.salary.dto;

import java.math.BigDecimal;
import java.util.List;

public record SalaryBreakdown(Period annual, Period monthly, List<ComponentAmount> components) {
    public record Period(
            BigDecimal ctc,
            BigDecimal basic,
            BigDecimal allowances,
            BigDecimal employerContributions,
            BigDecimal deductions,
            BigDecimal gross,
            BigDecimal net) {
    }

    public record ComponentAmount(
            String name,
            TemplateComponent.Category category,
            BigDecimal annual,
            BigDecimal monthly) {
    }
}
