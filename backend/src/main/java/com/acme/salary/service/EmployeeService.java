package com.acme.salary.service;
import com.acme.salary.dto.EmployeeRequest;
import com.acme.salary.dto.EmployeeResponse;
import com.acme.salary.dto.PageResponse;
import java.util.UUID;

public interface EmployeeService {
 PageResponse<EmployeeResponse> list(String query,String country,UUID department,String jobTitle,int page,int size);
 EmployeeResponse get(UUID id);
 EmployeeResponse create(EmployeeRequest request);
 EmployeeResponse update(UUID id,EmployeeRequest request);
 void archive(UUID id);
}
