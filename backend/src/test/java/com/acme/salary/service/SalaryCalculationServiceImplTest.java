package com.acme.salary.service;

import com.acme.salary.dto.TemplateComponent;
import com.acme.salary.validator.SalaryTemplateValidator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static com.acme.salary.dto.TemplateComponent.Category.*;
import static com.acme.salary.dto.TemplateComponent.Calculation.*;
import static org.junit.jupiter.api.Assertions.*;

class SalaryCalculationServiceImplTest {
    private final SalaryCalculationService calculator = new SalaryCalculationServiceImpl(new SalaryTemplateValidator());
    private TemplateComponent basic() { return new TemplateComponent("Basic", BASIC, PERCENT_CTC, new BigDecimal("40")); }
    private TemplateComponent balance() { return new TemplateComponent("Allowance", ALLOWANCE, BALANCE, BigDecimal.ZERO); }

    @Test void resolvesCtcWithEmployerContributionsAndEmployeeDeductions() {
        var result = calculator.calculate(new BigDecimal("1200000.00"), List.of(basic(), balance(),
                new TemplateComponent("Employer contribution", EMPLOYER_CONTRIBUTION, PERCENT_BASIC, new BigDecimal("12")),
                new TemplateComponent("Employee contribution", DEDUCTION, PERCENT_BASIC, new BigDecimal("12"))));
        assertEquals(new BigDecimal("100000.00"), result.monthly().ctc());
        assertEquals(new BigDecimal("40000.00"), result.monthly().basic());
        assertEquals(new BigDecimal("4800.00"), result.monthly().employerContributions());
        assertEquals(new BigDecimal("95200.00"), result.monthly().gross());
        assertEquals(new BigDecimal("90400.00"), result.monthly().net());
        assertEquals(result.monthly().ctc(), result.monthly().gross().add(result.monthly().employerContributions()));
    }
    @Test void balancingAllowanceAbsorbsMonthlyRounding() {
        var result = calculator.calculate(new BigDecimal("100000.01"), List.of(basic(), balance()));
        assertEquals(new BigDecimal("8333.33"), result.monthly().ctc());
        assertEquals(result.monthly().ctc(), result.monthly().gross());
        assertEquals(new BigDecimal("100000.01"), result.annual().gross());
    }
    @Test void rejectsFixedEarningsAboveCtc() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(new BigDecimal("100"), List.of(
                new TemplateComponent("Basic", BASIC, FIXED, new BigDecimal("200")), balance())));
    }
    @Test void rejectsNegativeNetAndCircularBasicRule() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(new BigDecimal("100"), List.of(basic(), balance(),
                new TemplateComponent("Deduction", DEDUCTION, FIXED, new BigDecimal("200")))));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(new BigDecimal("100"), List.of(
                new TemplateComponent("Basic", BASIC, PERCENT_BASIC, new BigDecimal("40")), balance())));
    }
    @Test void rejectsDuplicateNamesAndMissingBalancingAllowance() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(new BigDecimal("100"), List.of(basic())));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(new BigDecimal("100"), List.of(basic(), balance(),
                new TemplateComponent("basic", DEDUCTION, FIXED, BigDecimal.ZERO))));
    }
}
