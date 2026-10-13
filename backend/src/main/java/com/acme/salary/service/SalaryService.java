package com.acme.salary.service;
import com.acme.salary.dto.SalaryChangeRequest;
import com.acme.salary.dto.SalaryVersionResponse;
import java.util.List;
import java.util.UUID;
public interface SalaryService{
 List<SalaryVersionResponse> history(UUID employeeId);
 SalaryVersionResponse append(UUID employeeId,SalaryChangeRequest request);
}
