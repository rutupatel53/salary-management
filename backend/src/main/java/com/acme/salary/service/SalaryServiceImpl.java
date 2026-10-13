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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Service @Transactional
public class SalaryServiceImpl implements SalaryService{
 private final EmployeeRepository employees;private final SalaryVersionRepository salaries;private final SalaryVersionMapper mapper;private final AuditService audit;private final EntityManager entityManager;
 public SalaryServiceImpl(EmployeeRepository e,SalaryVersionRepository s,SalaryVersionMapper m,AuditService a,EntityManager entityManager){employees=e;salaries=s;mapper=m;audit=a;this.entityManager=entityManager;}
 @Override @Transactional(readOnly=true) public List<SalaryVersionResponse> history(UUID id){requireActive(id);return salaries.findByEmployeeIdOrderByEffectiveDateDesc(id).stream().map(mapper::toResponse).toList();}
 @Override public SalaryVersionResponse append(UUID id,SalaryChangeRequest r){
  Employee employee=requireActive(id);String currency=employee.getCountry().getCurrencyCode();
  if(!currency.equals(r.currencyCode()))throw new ConflictException("Salary currency must match the employee's country.");
  var latest=salaries.findTopByEmployeeIdOrderByEffectiveDateDesc(id);
  if(latest.isPresent()&&!r.effectiveDate().isAfter(latest.get().getEffectiveDate()))throw new ConflictException("Effective date must be later than the latest salary version.");
  BigDecimal basic=money(r.basic()),allowances=money(r.allowances()),deductions=money(r.deductions());
  if(basic.add(allowances).subtract(deductions).signum()<0)throw new ConflictException("Deductions cannot exceed gross pay.");
  SalaryVersion saved=salaries.saveAndFlush(new SalaryVersion(UUID.randomUUID(),id,r.effectiveDate(),currency,basic,allowances,deductions));
  entityManager.refresh(saved);
  audit.record("SALARY_CHANGED",id,Map.of("effectiveDate",r.effectiveDate().toString(),"currency",currency));return mapper.toResponse(saved);
 }
 private Employee requireActive(UUID id){return employees.findById(id).filter(e->e.getArchivedAt()==null).orElseThrow(()->new NotFoundException("Employee not found."));}
 private BigDecimal money(BigDecimal value){return value.setScale(2,RoundingMode.HALF_UP);}
}
