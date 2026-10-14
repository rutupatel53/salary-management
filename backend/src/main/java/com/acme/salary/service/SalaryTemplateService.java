package com.acme.salary.service;

import com.acme.salary.dto.*;
import com.acme.salary.enums.TemplateStatus;
import java.util.UUID;

public interface SalaryTemplateService {
    PageResponse<SalaryTemplateResponse> list(String query, String country, TemplateStatus status, int page, int size);
    SalaryTemplateResponse save(UUID id, SalaryTemplateRequest request);
    SalaryAssignmentPreview preview(SalaryAssignmentRequest request);
    SalaryVersionResponse assign(SalaryAssignmentRequest request);
}
