package com.acme.salary.service;

import com.acme.salary.audit.AuditService;
import com.acme.salary.dto.*;
import com.acme.salary.exception.ImportValidationException;
import com.acme.salary.model.Country;
import com.acme.salary.model.Department;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import com.acme.salary.repository.EmployeeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ExcelImportServiceImpl implements ExcelImportService{
 public static final List<String> HEADERS=List.of("Employee Number","First Name","Last Name","Work Email","Hire Date","Country Code","Department","Job Title","Currency","Basic","Allowances","Deductions","Salary Effective Date");
 private static final int MAX_BYTES=5*1024*1024,MAX_ROWS=500;
 private static final Pattern EMPLOYEE_NUMBER=Pattern.compile("[A-Za-z0-9-]{2,24}");
 private final EmployeeRepository employees;private final CountryRepository countries;private final DepartmentRepository departments;private final Validator validator;private final JdbcTemplate jdbc;private final AuditService audit;
 public ExcelImportServiceImpl(EmployeeRepository e,CountryRepository c,DepartmentRepository d,Validator v,JdbcTemplate jdbc,AuditService audit){employees=e;countries=c;departments=d;validator=v;this.jdbc=jdbc;this.audit=audit;}
 @Override public byte[] template(){
  try(Workbook workbook=new XSSFWorkbook();ByteArrayOutputStream out=new ByteArrayOutputStream()){
   Sheet sheet=workbook.createSheet("Employees");Row row=sheet.createRow(0);for(int i=0;i<HEADERS.size();i++){row.createCell(i,CellType.STRING).setCellValue(HEADERS.get(i));sheet.setColumnWidth(i,Math.min(HEADERS.get(i).length()+5,32)*256);}workbook.write(out);return out.toByteArray();
  }catch(IOException e){throw new IllegalStateException("Could not create the Excel template.",e);}
 }
 @Override @Transactional(readOnly=true) public ImportPreviewResponse preview(MultipartFile file){return validate(file).response();}
 @Override @Transactional public ImportConfirmResponse confirm(MultipartFile file){
  Validation result=validate(file);ImportPreviewResponse preview=result.response();
  if(preview.totalRows()==0||preview.invalidRows()>0||!preview.fileErrors().isEmpty())throw new ImportValidationException(preview);
  List<PreparedRow> rows=result.prepared();
  jdbc.batchUpdate("INSERT INTO employees(employee_id,employee_number,first_name,last_name,work_email,hire_date,country_code,department_id,job_title) VALUES (?,?,?,?,?,?,?,?,?)",rows,100,(ps,r)->{ps.setObject(1,r.employeeId());ps.setString(2,r.employee().employeeNumber());ps.setString(3,r.employee().firstName());ps.setString(4,r.employee().lastName());ps.setString(5,r.employee().workEmail());ps.setObject(6,r.employee().hireDate());ps.setString(7,r.employee().countryCode());ps.setObject(8,r.department().getId());ps.setString(9,r.employee().jobTitle());});
  jdbc.batchUpdate("INSERT INTO salary_versions(salary_version_id,employee_id,effective_date,currency_code,basic_amount,allowances_amount,deductions_amount) VALUES (?,?,?,?,?,?,?)",rows,100,(ps,r)->{ps.setObject(1,r.salaryId());ps.setObject(2,r.employeeId());ps.setObject(3,r.salary().effectiveDate());ps.setString(4,r.salary().currencyCode());ps.setBigDecimal(5,r.salary().basic());ps.setBigDecimal(6,r.salary().allowances());ps.setBigDecimal(7,r.salary().deductions());});
  audit.record("IMPORT_CONFIRMED",null,Map.of("rowCount",rows.size()));return new ImportConfirmResponse(rows.size(),"Import completed.");
 }

 private Validation validate(MultipartFile file){
  List<String> fileErrors=new ArrayList<>();List<PreparedRow> prepared=new ArrayList<>();List<ImportRowResult> results=new ArrayList<>();
  if(file==null||file.isEmpty())return new Validation(new ImportPreviewResponse(0,0,0,List.of("Choose an .xlsx workbook."),List.of()),prepared);
  String filename=Objects.toString(file.getOriginalFilename(),"");if(!filename.toLowerCase(Locale.ROOT).endsWith(".xlsx"))fileErrors.add("Only .xlsx workbooks are supported.");
  if(file.getSize()>MAX_BYTES)fileErrors.add("Workbook must be 5 MB or smaller.");if(!fileErrors.isEmpty())return new Validation(new ImportPreviewResponse(0,0,0,fileErrors,List.of()),prepared);
  try(Workbook workbook=WorkbookFactory.create(file.getInputStream())){
   if(workbook.getNumberOfSheets()==0){fileErrors.add("Workbook must have an Employees worksheet.");return new Validation(new ImportPreviewResponse(0,0,0,fileErrors,List.of()),prepared);}
   Sheet sheet=workbook.getSheetAt(0);if(sheet.getPhysicalNumberOfRows()==0){fileErrors.add("Workbook is empty.");return new Validation(new ImportPreviewResponse(0,0,0,fileErrors,List.of()),prepared);}
   DataFormatter formatter=new DataFormatter(Locale.US);Row header=sheet.getRow(0);
   for(int i=0;i<HEADERS.size();i++){String value=cellText(header==null?null:header.getCell(i),formatter);if(!HEADERS.get(i).equalsIgnoreCase(value.trim()))fileErrors.add("Column "+(i+1)+" must be '"+HEADERS.get(i)+"'.");}
   if(header!=null&&header.getLastCellNum()>HEADERS.size())fileErrors.add("Workbook has unexpected columns after 'Salary Effective Date'.");
   if(!fileErrors.isEmpty())return new Validation(new ImportPreviewResponse(0,0,0,fileErrors,List.of()),prepared);
   if(sheet.getLastRowNum()>MAX_ROWS){fileErrors.add("Workbook can contain at most "+MAX_ROWS+" employee rows.");return new Validation(new ImportPreviewResponse(0,0,0,fileErrors,List.of()),prepared);}
   Map<String,Department> departmentCache=new HashMap<>();Map<String,Integer> seenNumbers=new HashMap<>(),seenEmails=new HashMap<>();
   for(int index=1;index<=sheet.getLastRowNum();index++){
    Row row=sheet.getRow(index);if(isBlank(row,formatter))continue;List<String> errors=new ArrayList<>();int rowNumber=index+1;
    String number=cellText(row.getCell(0),formatter).trim(),first=cellText(row.getCell(1),formatter).trim(),last=cellText(row.getCell(2),formatter).trim(),email=cellText(row.getCell(3),formatter).trim().toLowerCase(Locale.ROOT);
    LocalDate hire=parseDate(row.getCell(4),formatter,"Hire date",errors);String countryCode=cellText(row.getCell(5),formatter).trim().toUpperCase(Locale.ROOT);String departmentName=cellText(row.getCell(6),formatter).trim();String title=cellText(row.getCell(7),formatter).trim();String currency=cellText(row.getCell(8),formatter).trim().toUpperCase(Locale.ROOT);
    BigDecimal basic=parseMoney(row.getCell(9),formatter,"Basic",errors),allowances=parseMoney(row.getCell(10),formatter,"Allowances",errors),deductions=parseMoney(row.getCell(11),formatter,"Deductions",errors);LocalDate effective=parseDate(row.getCell(12),formatter,"Salary effective date",errors);
    if(!EMPLOYEE_NUMBER.matcher(number).matches())errors.add("Employee number must be 2–24 letters, digits, or hyphens.");
    Department department=departmentCache.computeIfAbsent(departmentName.toLowerCase(Locale.ROOT),key->departments.findByNameIgnoreCaseAndActiveTrue(departmentName).orElse(null));
    if(departmentName.isBlank()||department==null)errors.add("Department must match an active department.");
    EmployeeRequest employee=new EmployeeRequest(number,first,last,email,hire,countryCode,department==null?null:department.getId(),title);
    for(ConstraintViolation<EmployeeRequest> violation:validator.validate(employee))errors.add(violation.getPropertyPath()+": "+violation.getMessage());
    Country country=countries.findById(countryCode).orElse(null);if(country==null)errors.add("Country code is not supported.");else if(!country.getCurrencyCode().equals(currency))errors.add("Currency must match the country's local currency.");
    if(basic!=null&&allowances!=null&&deductions!=null&&basic.add(allowances).subtract(deductions).signum()<0)errors.add("Deductions cannot exceed gross pay.");
    if(employees.existsByEmployeeNumberIgnoreCase(number))errors.add("Employee number already exists.");if(!email.isBlank()&&employees.existsByWorkEmailIgnoreCase(email))errors.add("Work email already exists.");
    Integer priorNumber=seenNumbers.putIfAbsent(number.toLowerCase(Locale.ROOT),rowNumber);if(priorNumber!=null)errors.add("Employee number duplicates row "+priorNumber+".");
    Integer priorEmail=seenEmails.putIfAbsent(email,rowNumber);if(priorEmail!=null)errors.add("Work email duplicates row "+priorEmail+".");
    if(errors.isEmpty()){
     EmployeeRequest normalized=new EmployeeRequest(number,first,last,email,hire,countryCode,department.getId(),title);
     SalaryChangeRequest salary=new SalaryChangeRequest(money(basic),money(allowances),money(deductions),currency,effective);
     for(ConstraintViolation<SalaryChangeRequest> violation:validator.validate(salary))errors.add(violation.getPropertyPath()+": "+violation.getMessage());
     if(errors.isEmpty())prepared.add(new PreparedRow(UUID.randomUUID(),UUID.randomUUID(),normalized,salary,department));
    }
    results.add(new ImportRowResult(rowNumber,number,errors.isEmpty(),List.copyOf(errors)));
   }
   if(results.isEmpty())fileErrors.add("Workbook must contain at least one employee row.");
  }catch(IOException|IllegalArgumentException e){fileErrors.add("Could not read this workbook. Download a fresh .xlsx template and try again.");}
  int valid=(int)results.stream().filter(ImportRowResult::valid).count();int invalid=results.size()-valid;
  return new Validation(new ImportPreviewResponse(results.size(),valid,invalid,List.copyOf(fileErrors),List.copyOf(results)),prepared);
 }
 private LocalDate parseDate(Cell cell,DataFormatter formatter,String label,List<String> errors){
  if(cell==null||cell.getCellType()==CellType.BLANK){errors.add(label+" is required.");return null;}
  try{if(cell.getCellType()==CellType.FORMULA)throw new IllegalArgumentException();if(cell.getCellType()==CellType.NUMERIC&&DateUtil.isCellDateFormatted(cell))return cell.getLocalDateTimeCellValue().toLocalDate();return LocalDate.parse(formatter.formatCellValue(cell).trim());}
  catch(Exception e){errors.add(label+" must be an Excel date or YYYY-MM-DD.");return null;}
 }
 private BigDecimal parseMoney(Cell cell,DataFormatter formatter,String label,List<String> errors){
  String value=cellText(cell,formatter).trim();try{if(value.isEmpty())throw new NumberFormatException();if(cell!=null&&cell.getCellType()==CellType.FORMULA)throw new NumberFormatException();return new BigDecimal(value).setScale(2,RoundingMode.HALF_UP);}
  catch(Exception e){errors.add(label+" must be a non-negative number with up to two decimal places.");return null;}
 }
 private String cellText(Cell cell,DataFormatter formatter){if(cell==null||cell.getCellType()==CellType.BLANK)return "";if(cell.getCellType()==CellType.FORMULA)return "";return formatter.formatCellValue(cell);}
 private boolean isBlank(Row row,DataFormatter formatter){if(row==null)return true;for(int i=0;i<HEADERS.size();i++)if(!cellText(row.getCell(i),formatter).isBlank())return false;return true;}
 private BigDecimal money(BigDecimal value){return value.setScale(2,RoundingMode.HALF_UP);}
 private record PreparedRow(UUID employeeId,UUID salaryId,EmployeeRequest employee,SalaryChangeRequest salary,Department department){}
 private record Validation(ImportPreviewResponse response,List<PreparedRow> prepared){}
}
