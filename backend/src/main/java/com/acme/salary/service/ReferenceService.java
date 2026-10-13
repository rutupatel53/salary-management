package com.acme.salary.service;

import com.acme.salary.dto.CountryResponse;
import com.acme.salary.dto.DepartmentResponse;
import java.util.List;

public interface ReferenceService {
    List<CountryResponse> countries();
    List<DepartmentResponse> departments();
}
