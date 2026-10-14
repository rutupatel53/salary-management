package com.acme.salary.dto;

import com.acme.salary.enums.TemplateStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SalaryTemplateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "US|IN|GB|DE|CA") String countryCode,
        @NotNull TemplateStatus status,
        @NotNull @Size(min = 1, max = 20) List<TemplateComponent> components,
        Long revision) {
}
