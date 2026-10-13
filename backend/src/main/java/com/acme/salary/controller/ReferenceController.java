package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ReferenceController {
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;

    public ReferenceController(CountryRepository countryRepository, DepartmentRepository departmentRepository) {
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/countries")
    public ApiResponse<List<CountryView>> countries() {
        List<CountryView> countries = countryRepository.findAllByOrderByName()
                .stream()
                .map(country -> new CountryView(country.getCode(), country.getName(), country.getCurrencyCode()))
                .toList();
        return ApiResponse.success(HttpStatus.OK.value(), countries, "Countries retrieved.");
    }

    @GetMapping("/departments")
    public ApiResponse<List<DepartmentView>> departments() {
        List<DepartmentView> departments = departmentRepository.findByActiveTrueOrderByName()
                .stream()
                .map(department -> new DepartmentView(department.getId(), department.getName()))
                .toList();
        return ApiResponse.success(HttpStatus.OK.value(), departments, "Departments retrieved.");
    }

    public record CountryView(String countryCode, String name, String currencyCode) { }

    public record DepartmentView(UUID id, String name) { }
}
