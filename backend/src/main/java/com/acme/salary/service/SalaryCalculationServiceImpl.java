package com.acme.salary.service;

import com.acme.salary.dto.SalaryBreakdown;
import com.acme.salary.dto.SalaryBreakdown.ComponentAmount;
import com.acme.salary.dto.SalaryBreakdown.Period;
import com.acme.salary.dto.TemplateComponent;
import com.acme.salary.validator.SalaryTemplateValidator;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import static com.acme.salary.dto.TemplateComponent.Calculation.*;
import static com.acme.salary.dto.TemplateComponent.Category.*;

@Service
public class SalaryCalculationServiceImpl implements SalaryCalculationService {
    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final BigDecimal MAX_CTC = new BigDecimal("999999999999.99");
    private final SalaryTemplateValidator validator;

    public SalaryCalculationServiceImpl(SalaryTemplateValidator validator) {
        this.validator = validator;
    }

    @Override
    public SalaryBreakdown calculate(BigDecimal annualCtc, List<TemplateComponent> components) {
        validator.validate(components);
        if (annualCtc == null || annualCtc.signum() <= 0 || annualCtc.scale() > 2 || annualCtc.compareTo(MAX_CTC) > 0) {
            throw new IllegalArgumentException("Annual CTC must be positive, with at most two decimals and within the supported range.");
        }
        CalculationResult annual = calculatePeriod(money(annualCtc), BigDecimal.ONE, components);
        CalculationResult monthly = calculatePeriod(annualCtc.divide(TWELVE, 2, RoundingMode.HALF_UP), TWELVE, components);
        var breakdown = new ArrayList<ComponentAmount>();
        for (int index = 0; index < components.size(); index++) {
            TemplateComponent component = components.get(index);
            breakdown.add(new ComponentAmount(component.name(), component.category(),
                    annual.amounts().get(index), monthly.amounts().get(index)));
        }
        return new SalaryBreakdown(annual.totals(), monthly.totals(), List.copyOf(breakdown));
    }

    private CalculationResult calculatePeriod(BigDecimal ctc, BigDecimal divisor, List<TemplateComponent> components) {
        TemplateComponent basicComponent = components.stream().filter(component -> component.category() == BASIC).findFirst().orElseThrow();
        BigDecimal basic = resolve(basicComponent, ctc, BigDecimal.ZERO, divisor);
        BigDecimal allowances = BigDecimal.ZERO;
        BigDecimal employer = BigDecimal.ZERO;
        BigDecimal deductions = BigDecimal.ZERO;
        int balanceIndex = -1;
        var amounts = new ArrayList<BigDecimal>();
        for (int index = 0; index < components.size(); index++) {
            TemplateComponent component = components.get(index);
            if (component.calculation() == BALANCE) {
                balanceIndex = index;
                amounts.add(BigDecimal.ZERO);
                continue;
            }
            BigDecimal amount = resolve(component, ctc, basic, divisor);
            amounts.add(amount);
            switch (component.category()) {
                case ALLOWANCE -> allowances = allowances.add(amount);
                case EMPLOYER_CONTRIBUTION -> employer = employer.add(amount);
                case DEDUCTION -> deductions = deductions.add(amount);
                case BASIC -> { }
            }
        }
        BigDecimal remainder = ctc.subtract(basic).subtract(allowances).subtract(employer);
        if (remainder.signum() < 0) {
            throw new IllegalArgumentException("Template earnings and employer contributions exceed CTC. Increase CTC or revise the template.");
        }
        amounts.set(balanceIndex, money(remainder));
        allowances = allowances.add(remainder);
        BigDecimal gross = basic.add(allowances);
        BigDecimal net = gross.subtract(deductions);
        if (net.signum() < 0) throw new IllegalArgumentException("Employee deductions cannot exceed gross pay.");
        return new CalculationResult(new Period(ctc, money(basic), money(allowances), money(employer),
                money(deductions), money(gross), money(net)), List.copyOf(amounts));
    }

    private BigDecimal resolve(TemplateComponent component, BigDecimal ctc, BigDecimal basic, BigDecimal divisor) {
        return switch (component.calculation()) {
            case FIXED -> component.value().divide(divisor, 2, RoundingMode.HALF_UP);
            case PERCENT_CTC -> money(ctc.multiply(component.value()).movePointLeft(2));
            case PERCENT_BASIC -> money(basic.multiply(component.value()).movePointLeft(2));
            case BALANCE -> throw new IllegalArgumentException("Balancing allowance must be calculated last.");
        };
    }

    private BigDecimal money(BigDecimal value) { return value.setScale(2, RoundingMode.HALF_UP); }
    private record CalculationResult(Period totals, List<BigDecimal> amounts) { }
}
