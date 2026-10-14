package com.acme.salary.service;

import com.acme.salary.dto.SalaryBreakdown;
import com.acme.salary.dto.TemplateComponent;
import java.math.BigDecimal;
import java.util.List;

public interface SalaryCalculationService {
    SalaryBreakdown calculate(BigDecimal annualCtc, List<TemplateComponent> components);
}
