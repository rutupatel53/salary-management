package com.acme.salary.controller;
import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.SalaryChangeRequest;
import com.acme.salary.dto.SalaryVersionResponse;
import com.acme.salary.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController @RequestMapping("/api/employees/{id}/salary-history")
public class SalaryController{
 private final SalaryService service;
 public SalaryController(SalaryService service){this.service=service;}
 @GetMapping public ApiResponse<List<SalaryVersionResponse>> history(@PathVariable UUID id){return new ApiResponse<>(service.history(id),null);}
 @PostMapping public ApiResponse<SalaryVersionResponse> append(@PathVariable UUID id,@Valid @RequestBody SalaryChangeRequest request){return new ApiResponse<>(service.append(id,request),"Salary version added.");}
}
