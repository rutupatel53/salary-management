package com.acme.salary.service;

import com.acme.salary.dto.EmployeeRequest;
import com.acme.salary.dto.EmployeeSort;
import com.acme.salary.dto.EmployeeResponse;
import com.acme.salary.dto.PageResponse;

import java.util.UUID;

public interface EmployeeService {
    PageResponse<EmployeeResponse> list(
            String query, String country, UUID departmentId, String jobTitle, int page, int size, EmployeeSort sort);

    EmployeeResponse get(UUID employeeId);

    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse update(UUID employeeId, EmployeeRequest request);

    void archive(UUID employeeId);
}
