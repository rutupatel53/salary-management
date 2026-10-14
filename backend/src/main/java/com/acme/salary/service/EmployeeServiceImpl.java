package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.EmployeeRequest;
import com.acme.salary.dto.EmployeeSort;
import com.acme.salary.dto.EmployeeResponse;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.exception.ConflictException;
import com.acme.salary.exception.NotFoundException;
import com.acme.salary.mapper.EmployeeMapper;
import com.acme.salary.model.Country;
import com.acme.salary.model.Department;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import com.acme.salary.repository.EmployeeRepository;
import com.acme.salary.specification.EmployeeSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final AuditService auditService;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            CountryRepository countryRepository,
            DepartmentRepository departmentRepository,
            EmployeeMapper employeeMapper,
            AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> list(
            String query, String country, UUID departmentId, String jobTitle, int page, int size, EmployeeSort sort) {
        var pageable = PageRequest.of(page, size,
                sort.toSort());
        var employees = employeeRepository.findAll(
                EmployeeSpecifications.active().and(
                        EmployeeSpecifications.filters(query, country, departmentId, jobTitle)),
                pageable);

        return new PageResponse<>(
                employees.getContent().stream().map(employeeMapper::toResponse).toList(),
                employees.getNumber(), employees.getSize(), employees.getTotalElements(), employees.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse get(UUID id) {
        return employeeMapper.toResponse(findActive(id));
    }

    @Override
    public EmployeeResponse create(EmployeeRequest request) {
        String employeeNumber = clean(request.employeeNumber());
        String email = normalizeEmail(request.workEmail());
        ensureEmployeeNumberAvailable(employeeNumber);
        ensureWorkEmailAvailable(email, null);

        Employee employee = new Employee(
                UUID.randomUUID(), employeeNumber, clean(request.firstName()), clean(request.lastName()), email,
                request.hireDate(), findCountry(request.countryCode()), findActiveDepartment(request.departmentId()),
                clean(request.jobTitle()));
        Employee savedEmployee = employeeRepository.save(employee);
        auditService.record("EMPLOYEE_CREATED", savedEmployee.getId(), Map.of("employeeNumber", employeeNumber));
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse update(UUID id, EmployeeRequest request) {
        Employee employee = findActive(id);
        String employeeNumber = clean(request.employeeNumber());
        if (!employee.getEmployeeNumber().equalsIgnoreCase(employeeNumber)) {
            throw new ConflictException("Employee number cannot be changed.");
        }

        String email = normalizeEmail(request.workEmail());
        ensureWorkEmailAvailable(email, id);
        employee.update(clean(request.firstName()), clean(request.lastName()), email, request.hireDate(),
                findCountry(request.countryCode()), findActiveDepartment(request.departmentId()),
                clean(request.jobTitle()));
        auditService.record("EMPLOYEE_UPDATED", id, Map.of("employeeNumber", employee.getEmployeeNumber()));
        return employeeMapper.toResponse(employee);
    }

    @Override
    public void archive(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found."));
        if (employee.getArchivedAt() == null) {
            employee.archive();
            auditService.record("EMPLOYEE_ARCHIVED", id,
                    Map.of("employeeNumber", employee.getEmployeeNumber()));
        }
    }

    private Employee findActive(UUID id) {
        return employeeRepository.findByIdAndArchivedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Employee not found."));
    }

    private Country findCountry(String countryCode) {
        return countryRepository.findById(countryCode)
                .orElseThrow(() -> new NotFoundException("Country is not supported."));
    }

    private Department findActiveDepartment(UUID departmentId) {
        return departmentRepository.findByIdAndActiveTrue(departmentId)
                .orElseThrow(() -> new NotFoundException("Department is not active."));
    }

    private void ensureEmployeeNumberAvailable(String employeeNumber) {
        if (employeeRepository.existsByEmployeeNumberIgnoreCase(employeeNumber)) {
            throw new ConflictException("Employee number is already in use.");
        }
    }

    private void ensureWorkEmailAvailable(String email, UUID excludedEmployeeId) {
        boolean alreadyInUse = excludedEmployeeId == null
                ? employeeRepository.existsByWorkEmailIgnoreCase(email)
                : employeeRepository.existsByWorkEmailIgnoreCaseAndIdNot(email, excludedEmployeeId);
        if (alreadyInUse) {
            throw new ConflictException("Work email is already in use.");
        }
    }

    private String normalizeEmail(String email) {
        return clean(email).toLowerCase(Locale.ROOT);
    }

    private String clean(String value) {
        return value.trim();
    }
}
