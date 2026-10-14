package com.acme.salary.service;

import com.acme.salary.dto.EmployeeSort;

import java.util.UUID;

public interface EmployeeExportService {
    byte[] export(String query, String country, UUID department, String jobTitle, EmployeeSort sort);
}
