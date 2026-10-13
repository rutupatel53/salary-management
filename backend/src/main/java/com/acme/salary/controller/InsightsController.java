package com.acme.salary.controller;
import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.PayInsightsResponse;
import com.acme.salary.dto.SalaryOutlierResponse;
import com.acme.salary.service.InsightsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController @RequestMapping("/api/insights")
public class InsightsController{
 private final InsightsService service;
 public InsightsController(InsightsService service){this.service=service;}
 @GetMapping("/pay") public ApiResponse<PayInsightsResponse> pay(@RequestParam(defaultValue="country") String groupBy,@RequestParam(required=false) String country,@RequestParam(required=false) String department,@RequestParam(required=false) String jobTitle){return new ApiResponse<>(service.pay(groupBy,validCountry(country),uuid(department),jobTitle),null);}
 @GetMapping("/outliers") public ApiResponse<List<SalaryOutlierResponse>> outliers(@RequestParam(required=false) String country,@RequestParam(required=false) String department,@RequestParam(required=false) String jobTitle){return new ApiResponse<>(service.outliers(validCountry(country),uuid(department),jobTitle),null);}
 private String validCountry(String country){if(country!=null&&!List.of("US","IN","GB","DE","CA").contains(country))throw new IllegalArgumentException("Country must be one of the supported country codes.");return country;}
 private UUID uuid(String value){if(value==null||value.isBlank())return null;try{return UUID.fromString(value);}catch(IllegalArgumentException ex){throw new IllegalArgumentException("Department must be a valid UUID.");}}
}
