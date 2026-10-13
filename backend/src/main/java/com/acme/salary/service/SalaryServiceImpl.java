package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.SalaryChangeRequest;
import com.acme.salary.dto.SalaryVersionResponse;
import com.acme.salary.exception.ConflictException;
import com.acme.salary.exception.NotFoundException;
import com.acme.salary.mapper.SalaryVersionMapper;
import com.acme.salary.model.Employee;
import com.acme.salary.model.SalaryVersion;
import com.acme.salary.repository.EmployeeRepository;
import com.acme.salary.repository.SalaryVersionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class SalaryServiceImpl implements SalaryService {
    private final EmployeeRepository employeeRepository;
    private final SalaryVersionRepository salaryVersionRepository;
    private final SalaryVersionMapper salaryVersionMapper;
    private final AuditService auditService;
    private final EntityManager entityManager;

    public SalaryServiceImpl(
            EmployeeRepository employeeRepository,
            SalaryVersionRepository salaryVersionRepository,
            SalaryVersionMapper salaryVersionMapper,
            AuditService auditService,
            EntityManager entityManager) {
        this.employeeRepository = employeeRepository;
        this.salaryVersionRepository = salaryVersionRepository;
        this.salaryVersionMapper = salaryVersionMapper;
        this.auditService = auditService;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryVersionResponse> history(UUID employeeId) {
        requireActiveEmployee(employeeId);
        return salaryVersionRepository.findByEmployeeIdOrderByEffectiveDateDesc(employeeId)
                .stream()
                .map(salaryVersionMapper::toResponse)
                .toList();
    }

    @Override
    public SalaryVersionResponse append(UUID employeeId, SalaryChangeRequest request) {
        Employee employee = requireActiveEmployee(employeeId);
        String currency = employee.getCountry().getCurrencyCode();
        if (!currency.equals(request.currencyCode())) {
            throw new ConflictException("Salary currency must match the employee's country.");
        }

        salaryVersionRepository.findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId)
                .ifPresent(latest -> validateEffectiveDate(request, latest));

        BigDecimal basic = money(request.basic());
        BigDecimal allowances = money(request.allowances());
        BigDecimal deductions = money(request.deductions());
        if (basic.add(allowances).subtract(deductions).signum() < 0) {
            throw new ConflictException("Deductions cannot exceed gross pay.");
        }

        SalaryVersion savedVersion = salaryVersionRepository.saveAndFlush(new SalaryVersion(
                UUID.randomUUID(), employeeId, request.effectiveDate(), currency, basic, allowances, deductions));
        entityManager.refresh(savedVersion);
        auditService.record("SALARY_CHANGED", employeeId,
                Map.of("effectiveDate", request.effectiveDate().toString(), "currency", currency));
        return salaryVersionMapper.toResponse(savedVersion);
    }

    private void validateEffectiveDate(SalaryChangeRequest request, SalaryVersion latest) {
        if (!request.effectiveDate().isAfter(latest.getEffectiveDate())) {
            throw new ConflictException("Effective date must be later than the latest salary version.");
        }
    }

    private Employee requireActiveEmployee(UUID employeeId) {
        return employeeRepository.findByIdAndArchivedAtIsNull(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found."));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
