package com.acme.salary.service;
import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.SalaryChangeRequest;
import com.acme.salary.dto.SalaryVersionResponse;
import com.acme.salary.exception.ConflictException;
import com.acme.salary.mapper.SalaryVersionMapper;
import com.acme.salary.model.Country;
import com.acme.salary.model.Employee;
import com.acme.salary.model.SalaryVersion;
import com.acme.salary.repository.EmployeeRepository;
import com.acme.salary.repository.SalaryVersionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class SalaryServiceImplTest{
 private EmployeeRepository employees;private SalaryVersionRepository salaries;private SalaryVersionMapper mapper;private AuditService audit;private EntityManager entityManager;private SalaryServiceImpl service;private UUID employeeId;
 @BeforeEach void setup(){
  employees=mock(EmployeeRepository.class);salaries=mock(SalaryVersionRepository.class);mapper=mock(SalaryVersionMapper.class);audit=mock(AuditService.class);entityManager=mock(EntityManager.class);service=new SalaryServiceImpl(employees,salaries,mapper,audit,entityManager);employeeId=UUID.randomUUID();
  Employee employee=mock(Employee.class);Country country=mock(Country.class);when(employee.getCountry()).thenReturn(country);when(country.getCurrencyCode()).thenReturn("INR");when(employees.findByIdAndArchivedAtIsNull(employeeId)).thenReturn(Optional.of(employee));when(employees.findActiveForSalaryUpdate(employeeId)).thenReturn(Optional.of(employee));when(salaries.findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId)).thenReturn(Optional.empty());
 }
 private SalaryChangeRequest request(String basic,String allowances,String deductions,String effective){return new SalaryChangeRequest(new BigDecimal(basic),new BigDecimal(allowances),new BigDecimal(deductions),"INR",LocalDate.parse(effective));}
 @Test void appendsRoundedSnapshotAndAuditsChange(){
  when(salaries.saveAndFlush(any())).thenAnswer(i->i.getArgument(0));when(mapper.toResponse(any())).thenReturn(new SalaryVersionResponse(UUID.randomUUID(),LocalDate.parse("2025-01-01"),"INR",new BigDecimal("1000.13"),new BigDecimal("100.00"),new BigDecimal("50.00"),new BigDecimal("1100.13"),new BigDecimal("1050.13"),null,null,null,null));
  var result=service.append(employeeId,request("1000.125","100","50","2025-01-01"));assertEquals(new BigDecimal("1050.13"),result.net());
  verify(salaries).saveAndFlush(argThat(s->s.getBasic().equals(new BigDecimal("1000.13"))));verify(entityManager).refresh(any(SalaryVersion.class));verify(audit).record(eq("SALARY_CHANGED"),eq(employeeId),anyMap());
 }
 @Test void rejectsNegativeNetBeforeWriting(){
  assertThrows(ConflictException.class,()->service.append(employeeId,request("100","10","111","2025-01-01")));verify(salaries,never()).saveAndFlush(any());
 }
 @Test void rejectsEffectiveDateNotAfterLatestVersion(){
  SalaryVersion latest=mockVersion("2025-03-01");when(salaries.findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId)).thenReturn(Optional.of(latest));
  assertThrows(ConflictException.class,()->service.append(employeeId,request("100","10","1","2025-03-01")));verify(salaries,never()).saveAndFlush(any());
 }
 private SalaryVersion mockVersion(String date){SalaryVersion version=mock(SalaryVersion.class);when(version.getEffectiveDate()).thenReturn(LocalDate.parse(date));return version;}
}
