package com.acme.salary.dto;

import java.math.BigDecimal;

public record TemplateComponent(
        String name,
        Category category,
        Calculation calculation,
        BigDecimal value) {

    public enum Category {
        BASIC,
        ALLOWANCE,
        EMPLOYER_CONTRIBUTION,
        DEDUCTION
    }

    public enum Calculation {
        FIXED,
        PERCENT_CTC,
        PERCENT_BASIC,
        BALANCE
    }
}
