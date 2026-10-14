package com.acme.salary.validator;

import com.acme.salary.dto.TemplateComponent;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import static com.acme.salary.dto.TemplateComponent.Calculation.*;
import static com.acme.salary.dto.TemplateComponent.Category.*;

@Component
public class SalaryTemplateValidator {
    public void validate(List<TemplateComponent> components) {
        if (components == null || components.isEmpty() || components.size() > 20) {
            throw new IllegalArgumentException("Add between 1 and 20 salary components.");
        }
        int basics = 0;
        int balances = 0;
        var names = new HashSet<String>();
        for (TemplateComponent component : components) {
            if (component == null || component.name() == null || component.name().isBlank()
                    || component.name().length() > 80 || component.category() == null
                    || component.calculation() == null || component.value() == null) {
                throw new IllegalArgumentException("Each component needs a name, category, calculation and value.");
            }
            if (!names.add(component.name().trim().toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Component names must be unique.");
            }
            BigDecimal value = component.value();
            if (value.signum() < 0 || value.scale() > 2 || value.compareTo(new BigDecimal("999999999999.99")) > 0) {
                throw new IllegalArgumentException("Component values must be nonnegative with at most two decimals.");
            }
            if ((component.calculation() == PERCENT_CTC || component.calculation() == PERCENT_BASIC)
                    && value.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("Percentages cannot exceed 100.");
            }
            if (component.category() == BASIC) {
                basics++;
                if (component.calculation() != FIXED && component.calculation() != PERCENT_CTC) {
                    throw new IllegalArgumentException("Basic pay must be fixed or a percentage of CTC.");
                }
            }
            if (component.calculation() == BALANCE) {
                balances++;
                if (component.category() != ALLOWANCE || value.signum() != 0) {
                    throw new IllegalArgumentException("The balancing component must be an allowance with value zero.");
                }
            }
        }
        if (basics != 1 || balances != 1) {
            throw new IllegalArgumentException("Use exactly one basic component and one balancing allowance.");
        }
    }
}
