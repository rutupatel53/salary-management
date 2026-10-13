package com.acme.salary.service;

import com.acme.salary.dto.CountryResponse;
import com.acme.salary.dto.DepartmentResponse;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReferenceServiceImpl implements ReferenceService {
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;

    public ReferenceServiceImpl(CountryRepository countryRepository, DepartmentRepository departmentRepository) {
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<CountryResponse> countries() {
        return countryRepository.findCountryOptions();
    }

    @Override
    public List<DepartmentResponse> departments() {
        return departmentRepository.findActiveDepartmentOptions();
    }
}
