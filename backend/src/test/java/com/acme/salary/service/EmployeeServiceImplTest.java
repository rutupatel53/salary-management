package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.EmployeeRequest;
import com.acme.salary.dto.EmployeeResponse;
import com.acme.salary.exception.ConflictException;
import com.acme.salary.mapper.EmployeeMapper;
import com.acme.salary.model.Country;
import com.acme.salary.model.Department;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import com.acme.salary.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {
 private EmployeeRepository employees; private CountryRepository countries; private DepartmentRepository departments;
 private EmployeeMapper mapper; private AuditService audit; private EmployeeServiceImpl service;
 private final UUID departmentId=UUID.fromString("00000000-0000-5000-8000-000000000001");
 @BeforeEach void setUp(){
  employees=mock(EmployeeRepository.class);countries=mock(CountryRepository.class);departments=mock(DepartmentRepository.class);mapper=mock(EmployeeMapper.class);audit=mock(AuditService.class);
  service=new EmployeeServiceImpl(employees,countries,departments,mapper,audit);
  when(countries.findById("IN")).thenReturn(Optional.of(mock(Country.class)));
  Department department=mock(Department.class);when(department.isActive()).thenReturn(true);when(departments.findByIdAndActiveTrue(departmentId)).thenReturn(Optional.of(department));
 }
 @Test void createNormalizesIdentityAndRecordsAudit(){
  var request=new EmployeeRequest("EMP-1"," Riya "," Shah "," RIYA@EXAMPLE.COM ",LocalDate.of(2020,1,2),"IN",departmentId," Engineer ");
  when(employees.existsByEmployeeNumberIgnoreCase("EMP-1")).thenReturn(false);when(employees.existsByWorkEmailIgnoreCase("riya@example.com")).thenReturn(false);
  when(employees.save(any(Employee.class))).thenAnswer(i->i.getArgument(0));
  when(mapper.toResponse(any())).thenReturn(new EmployeeResponse(UUID.randomUUID(),"EMP-1","Riya","Shah","riya@example.com",request.hireDate(),"IN","India",departmentId,"Engineering","Engineer","INR"));
  var result=service.create(request);
  assertEquals("EMP-1",result.employeeNumber());
  verify(employees).save(argThat(e->e.getWorkEmail().equals("riya@example.com")&&e.getFirstName().equals("Riya")));
  verify(audit).record(eq("EMPLOYEE_CREATED"),any(UUID.class),anyMap());
 }
 @Test void duplicateEmailIsRejectedBeforeWrite(){
  var request=new EmployeeRequest("EMP-1","Riya","Shah","riya@example.com",LocalDate.of(2020,1,2),"IN",departmentId,"Engineer");
  when(employees.existsByEmployeeNumberIgnoreCase("EMP-1")).thenReturn(false);when(employees.existsByWorkEmailIgnoreCase("riya@example.com")).thenReturn(true);
  assertThrows(ConflictException.class,()->service.create(request));verify(employees,never()).save(any());
 }
}
