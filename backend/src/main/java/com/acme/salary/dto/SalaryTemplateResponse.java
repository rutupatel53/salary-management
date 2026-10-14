package com.acme.salary.dto;

import com.acme.salary.enums.TemplateStatus;

import java.util.List;
import java.util.UUID;

public record SalaryTemplateResponse(
        UUID id,
        String name,
        String countryCode,
        String currencyCode,
        TemplateStatus status,
        long revision,
        List<TemplateComponent> components) {
}
