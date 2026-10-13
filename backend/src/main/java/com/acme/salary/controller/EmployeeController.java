package com.acme.salary.controller;
import com.acme.salary.dto.*;
import com.acme.salary.service.EmployeeService;
import com.acme.salary.service.EmployeeExportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/employees")
public class EmployeeController {
 private final EmployeeService service;
 private final EmployeeExportService exportService;
 public EmployeeController(EmployeeService service,EmployeeExportService exportService){this.service=service;this.exportService=exportService;}
 @GetMapping public PageResponse<EmployeeResponse> list(@RequestParam(required=false) String q,@RequestParam(required=false) String country,
   @RequestParam(required=false) String department,@RequestParam(required=false) String jobTitle,
   @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size){
  if(page<0)throw new IllegalArgumentException("Page must be zero or greater.");
  if(size<1||size>100)throw new IllegalArgumentException("Page size must be between 1 and 100.");
  UUID departmentId=null;
  if(department!=null&&!department.isBlank())try{departmentId=UUID.fromString(department);}catch(IllegalArgumentException e){throw new IllegalArgumentException("Department filter must be a UUID.");}
  return service.list(q,country,departmentId,jobTitle,page,size);
 }
 @GetMapping("/{id}") public ApiResponse<EmployeeResponse> get(@PathVariable UUID id){return new ApiResponse<>(service.get(id),null);}
 @GetMapping("/export") public org.springframework.http.ResponseEntity<byte[]> export(@RequestParam(required=false) String q,@RequestParam(required=false) String country,
   @RequestParam(required=false) String department,@RequestParam(required=false) String jobTitle){
  UUID departmentId=null;if(department!=null&&!department.isBlank())try{departmentId=UUID.fromString(department);}catch(IllegalArgumentException e){throw new IllegalArgumentException("Department filter must be a UUID.");}
  byte[] content=exportService.export(q,country,departmentId,jobTitle);
  return org.springframework.http.ResponseEntity.ok().contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
   .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=salary-management-employees.xlsx").body(content);
 }
 @PostMapping public ApiResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request){return new ApiResponse<>(service.create(request),"Employee added.");}
 @PutMapping("/{id}") public ApiResponse<EmployeeResponse> update(@PathVariable UUID id,@Valid @RequestBody EmployeeRequest request){return new ApiResponse<>(service.update(id,request),"Employee saved.");}
 @DeleteMapping("/{id}") public ApiResponse<Void> archive(@PathVariable UUID id){service.archive(id);return new ApiResponse<>(null,"Employee archived.");}
}
