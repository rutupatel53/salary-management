package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.EmployeeRequest;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
 private final EmployeeRepository employees; private final CountryRepository countries;
 private final DepartmentRepository departments; private final EmployeeMapper mapper; private final AuditService audit;
 public EmployeeServiceImpl(EmployeeRepository employees,CountryRepository countries,DepartmentRepository departments,EmployeeMapper mapper,AuditService audit){
  this.employees=employees;this.countries=countries;this.departments=departments;this.mapper=mapper;this.audit=audit;
 }
 @Override @Transactional(readOnly=true)
 public PageResponse<EmployeeResponse> list(String query,String country,UUID department,String jobTitle,int page,int size){
  var pageable=PageRequest.of(page,size,Sort.by("employeeNumber").ascending().and(Sort.by("id").ascending()));
  var result=employees.findAll(EmployeeSpecifications.active().and(EmployeeSpecifications.filters(query,country,department,jobTitle)),pageable);
  return new PageResponse<>(result.getContent().stream().map(mapper::toResponse).toList(),result.getNumber(),result.getSize(),result.getTotalElements(),result.getTotalPages());
 }
 @Override @Transactional(readOnly=true) public EmployeeResponse get(UUID id){return mapper.toResponse(findActive(id));}
 @Override public EmployeeResponse create(EmployeeRequest r){
  String number=clean(r.employeeNumber());String email=clean(r.workEmail()).toLowerCase();
  if(employees.existsByEmployeeNumberIgnoreCase(number))throw new ConflictException("Employee number is already in use.");
  if(employees.existsByWorkEmailIgnoreCase(email))throw new ConflictException("Work email is already in use.");
  Employee employee=new Employee(UUID.randomUUID(),number,clean(r.firstName()),clean(r.lastName()),email,r.hireDate(),country(r.countryCode()),department(r.departmentId()),clean(r.jobTitle()));
  employee=employees.save(employee);audit.record("EMPLOYEE_CREATED",employee.getId(),Map.of("employeeNumber",number));return mapper.toResponse(employee);
 }
 @Override public EmployeeResponse update(UUID id,EmployeeRequest r){
  Employee employee=findActive(id);
  if(!employee.getEmployeeNumber().equalsIgnoreCase(clean(r.employeeNumber())))throw new ConflictException("Employee number cannot be changed.");
  String email=clean(r.workEmail()).toLowerCase();
  if(employees.existsByWorkEmailIgnoreCaseAndIdNot(email,id))throw new ConflictException("Work email is already in use.");
  employee.update(clean(r.firstName()),clean(r.lastName()),email,r.hireDate(),country(r.countryCode()),department(r.departmentId()),clean(r.jobTitle()));
  audit.record("EMPLOYEE_UPDATED",id,Map.of("employeeNumber",employee.getEmployeeNumber()));return mapper.toResponse(employee);
 }
 @Override public void archive(UUID id){
  Employee employee=employees.findById(id).orElseThrow(()->new NotFoundException("Employee not found."));
  if(employee.getArchivedAt()==null){employee.archive();audit.record("EMPLOYEE_ARCHIVED",id,Map.of("employeeNumber",employee.getEmployeeNumber()));}
 }
 private Employee findActive(UUID id){return employees.findById(id).filter(e->e.getArchivedAt()==null).orElseThrow(()->new NotFoundException("Employee not found."));}
 private Country country(String code){return countries.findById(code).orElseThrow(()->new NotFoundException("Country is not supported."));}
 private Department department(UUID id){return departments.findById(id).filter(Department::isActive).orElseThrow(()->new NotFoundException("Department is not active."));}
 private String clean(String s){return s.trim();}
}
