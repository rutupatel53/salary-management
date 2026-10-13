package com.acme.salary.seed;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class TestDataResetService{
 private static final UUID DEPARTMENT=UUID.fromString("00000000-0000-5000-8000-000000000001");
 private final JdbcTemplate jdbc;
 public TestDataResetService(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @Transactional public int reset(){
  jdbc.update("DELETE FROM activity_log WHERE employee_id IN (SELECT employee_id FROM employees WHERE employee_number LIKE 'PW-%')");
  jdbc.update("DELETE FROM salary_versions WHERE employee_id IN (SELECT employee_id FROM employees WHERE employee_number LIKE 'PW-%')");
  jdbc.update("DELETE FROM employees WHERE employee_number LIKE 'PW-%'");
  String[] titles={"Software Engineer","Software Engineer","Software Engineer","Software Engineer","Software Engineer","Software Engineer"};
  BigDecimal[] basics={new BigDecimal("5000.00"),new BigDecimal("5200.00"),new BigDecimal("5400.00"),new BigDecimal("5600.00"),new BigDecimal("5800.00"),new BigDecimal("25000.00")};
  List<Row> rows=new ArrayList<>();
  for(int i=0;i<basics.length;i++){
   String number=String.format("PW-%03d",i+1);String sequence=String.format("%012d",i+1);UUID id=UUID.fromString("10000000-0000-5000-8000-"+sequence);BigDecimal allowance=new BigDecimal("500.00"),deduction=new BigDecimal("300.00");
   rows.add(new Row(id,number,"Test"+i,"Person"+i,"pw-"+String.format("%03d",i+1)+"@example.test",LocalDate.of(2020,1,1).plusDays(i),titles[i],basics[i],allowance,deduction,UUID.fromString("20000000-0000-5000-8000-"+sequence)));
  }
  jdbc.batchUpdate("INSERT INTO employees(employee_id,employee_number,first_name,last_name,work_email,hire_date,country_code,department_id,job_title) VALUES (?,?,?,?,?,?,?,?,?)",rows,100,(ps,r)->{ps.setObject(1,r.id);ps.setString(2,r.number);ps.setString(3,r.first);ps.setString(4,r.last);ps.setString(5,r.email);ps.setObject(6,r.hire);ps.setString(7,"IN");ps.setObject(8,DEPARTMENT);ps.setString(9,r.title);});
  jdbc.batchUpdate("INSERT INTO salary_versions(salary_version_id,employee_id,effective_date,currency_code,basic_amount,allowances_amount,deductions_amount) VALUES (?,?,?,'INR',?,?,?)",rows,100,(ps,r)->{ps.setObject(1,r.salaryId);ps.setObject(2,r.id);ps.setObject(3,LocalDate.of(2025,1,1));ps.setBigDecimal(4,r.basic);ps.setBigDecimal(5,r.allowance);ps.setBigDecimal(6,r.deduction);});
  return rows.size();
 }
 private record Row(UUID id,String number,String first,String last,String email,LocalDate hire,String title,BigDecimal basic,BigDecimal allowance,BigDecimal deduction,UUID salaryId){}
}
