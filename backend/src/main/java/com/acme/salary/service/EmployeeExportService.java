package com.acme.salary.service;

import java.util.UUID;

public interface EmployeeExportService {
    byte[] export(String query, String country, UUID department, String jobTitle);
}
