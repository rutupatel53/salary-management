package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.dto.SalaryAssignmentPreview;
import com.acme.salary.dto.SalaryAssignmentRequest;
import com.acme.salary.dto.SalaryBreakdown;
import com.acme.salary.dto.SalaryTemplateRequest;
import com.acme.salary.dto.SalaryTemplateResponse;
import com.acme.salary.dto.SalaryVersionResponse;
import com.acme.salary.enums.TemplateStatus;
import com.acme.salary.exception.ConflictException;
import com.acme.salary.exception.NotFoundException;
import com.acme.salary.mapper.EmployeeMapper;
import com.acme.salary.mapper.SalaryTemplateMapper;
import com.acme.salary.mapper.SalaryVersionMapper;
import com.acme.salary.model.Employee;
import com.acme.salary.model.SalaryTemplate;
import com.acme.salary.model.SalaryVersion;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.EmployeeRepository;
import com.acme.salary.repository.SalaryTemplateRepository;
import com.acme.salary.repository.SalaryVersionRepository;
import com.acme.salary.validator.SalaryTemplateValidator;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class SalaryTemplateServiceImpl implements SalaryTemplateService {
    private final SalaryTemplateRepository templates;
    private final CountryRepository countries;
    private final EmployeeRepository employees;
    private final SalaryVersionRepository salaries;
    private final SalaryCalculationService calculator;
    private final SalaryTemplateValidator validator;
    private final SalaryTemplateMapper templateMapper;
    private final EmployeeMapper employeeMapper;
    private final SalaryVersionMapper salaryMapper;
    private final AuditService audit;
    private final EntityManager entityManager;

    public SalaryTemplateServiceImpl(
            SalaryTemplateRepository templates,
            CountryRepository countries,
            EmployeeRepository employees,
            SalaryVersionRepository salaries,
            SalaryCalculationService calculator,
            SalaryTemplateValidator validator,
            SalaryTemplateMapper templateMapper,
            EmployeeMapper employeeMapper,
            SalaryVersionMapper salaryMapper,
            AuditService audit,
            EntityManager entityManager) {
        this.templates = templates;
        this.countries = countries;
        this.employees = employees;
        this.salaries = salaries;
        this.calculator = calculator;
        this.validator = validator;
        this.templateMapper = templateMapper;
        this.employeeMapper = employeeMapper;
        this.salaryMapper = salaryMapper;
        this.audit = audit;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SalaryTemplateResponse> list(
            String query, String country, TemplateStatus status, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be 1 to 100 and page nonnegative.");
        }

        var results = templates.search(
                query == null ? "" : query.trim(),
                country,
                status,
                PageRequest.of(page, size, Sort.by("name").and(Sort.by("id"))));
        return new PageResponse<>(
                results.map(templateMapper::toResponse).getContent(),
                page,
                size,
                results.getTotalElements(),
                results.getTotalPages());
    }

    @Override
    public SalaryTemplateResponse save(UUID id, SalaryTemplateRequest request) {
        validator.validate(request.components());
        var country = countries.findById(request.countryCode())
                .orElseThrow(() -> new IllegalArgumentException("Unsupported country."));
        SalaryTemplate template = loadTemplateForSave(id);

        if (id != null && (request.revision() == null || request.revision() != template.getRevision())) {
            throw new ConflictException("Template changed. Reload it before saving.");
        }

        template.revise(
                request.name(),
                country.getCode(),
                country.getCurrencyCode(),
                request.status(),
                request.components());
        SalaryTemplate saved = templates.saveAndFlush(template);

        audit.record(
                id == null ? "TEMPLATE_CREATED" : "TEMPLATE_UPDATED",
                null,
                Map.of(
                        "templateId", saved.getId().toString(),
                        "name", saved.getName(),
                        "revision", saved.getRevision()));
        return templateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryAssignmentPreview preview(SalaryAssignmentRequest request) {
        Employee employee = employees.findByIdAndArchivedAtIsNull(request.employeeId())
                .orElseThrow(() -> new NotFoundException("Active employee not found."));
        SalaryTemplate template = templates.findById(request.templateId())
                .orElseThrow(() -> new NotFoundException("Salary template not found."));
        return prepare(employee, template, request);
    }

    @Override
    public SalaryVersionResponse assign(SalaryAssignmentRequest request) {
        Employee employee = employees.findActiveForSalaryUpdate(request.employeeId())
                .orElseThrow(() -> new NotFoundException("Active employee not found."));
        SalaryTemplate template = templates.findForAssignment(request.templateId())
                .orElseThrow(() -> new NotFoundException("Salary template not found."));
        SalaryAssignmentPreview preview = prepare(employee, template, request);
        SalaryBreakdown.Period monthly = preview.breakdown().monthly();

        SalaryVersion version = new SalaryVersion(
                UUID.randomUUID(),
                employee.getId(),
                request.effectiveDate(),
                template.getCurrencyCode(),
                monthly.basic(),
                monthly.allowances(),
                monthly.deductions());
        version.setAssignmentSnapshot(
                template.getId(),
                template.getName(),
                template.getRevision(),
                request.annualCtc(),
                preview.breakdown());

        SalaryVersion saved = salaries.saveAndFlush(version);
        entityManager.refresh(saved);
        audit.record(
                "SALARY_CHANGED",
                employee.getId(),
                Map.of(
                        "templateId", template.getId().toString(),
                        "effectiveDate", request.effectiveDate().toString(),
                        "currency", template.getCurrencyCode()));
        return salaryMapper.toResponse(saved);
    }

    private SalaryTemplate loadTemplateForSave(UUID id) {
        if (id == null) {
            return new SalaryTemplate(UUID.randomUUID());
        }
        return templates.findById(id)
                .orElseThrow(() -> new NotFoundException("Salary template not found."));
    }

    private SalaryAssignmentPreview prepare(
            Employee employee, SalaryTemplate template, SalaryAssignmentRequest request) {
        validateAssignment(employee, template, request);
        var latest = salaries.findTopByEmployeeIdOrderByEffectiveDateDesc(employee.getId());

        if (request.effectiveDate().isBefore(employee.getHireDate())) {
            throw new ConflictException("Effective date cannot be before hire date.");
        }
        latest.ifPresent(previous -> {
            if (!request.effectiveDate().isAfter(previous.getEffectiveDate())) {
                throw new ConflictException(
                        "Effective date must follow the latest salary date: " + previous.getEffectiveDate());
            }
        });

        return new SalaryAssignmentPreview(
                employeeMapper.toResponse(employee),
                templateMapper.toResponse(template),
                latest.map(salaryMapper::toResponse).orElse(null),
                calculator.calculate(request.annualCtc(), template.getComponents()));
    }

    private void validateAssignment(
            Employee employee, SalaryTemplate template, SalaryAssignmentRequest request) {
        if (template.getStatus() != TemplateStatus.ACTIVE) {
            throw new ConflictException("Choose an active salary template.");
        }
        if (request.templateRevision() == null || template.getRevision() != request.templateRevision()) {
            throw new ConflictException("Template changed. Choose it again and review a fresh preview.");
        }
        if (!employee.getCountry().getCode().equals(template.getCountryCode())) {
            throw new ConflictException("Template country must match the employee country.");
        }
    }
}
