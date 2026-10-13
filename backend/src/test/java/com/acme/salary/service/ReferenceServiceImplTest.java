package com.acme.salary.service;

import com.acme.salary.dto.CountryResponse;
import com.acme.salary.dto.DepartmentResponse;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReferenceServiceImplTest {
    private final CountryRepository countries = mock(CountryRepository.class);
    private final DepartmentRepository departments = mock(DepartmentRepository.class);
    private final ReferenceService service = new ReferenceServiceImpl(countries, departments);

    @Test
    void countriesUseTheOrderedDtoProjection() {
        var expected = List.of(new CountryResponse("IN", "India", "INR"));
        when(countries.findCountryOptions()).thenReturn(expected);

        assertEquals(expected, service.countries());

        verify(countries).findCountryOptions();
        verifyNoMoreInteractions(countries);
        verifyNoInteractions(departments);
    }

    @Test
    void departmentsUseTheActiveOrderedDtoProjection() {
        UUID id = UUID.fromString("10000000-0000-5000-8000-000000000001");
        var expected = List.of(new DepartmentResponse(id, "Engineering"));
        when(departments.findActiveDepartmentOptions()).thenReturn(expected);

        assertEquals(expected, service.departments());

        verify(departments).findActiveDepartmentOptions();
        verifyNoMoreInteractions(departments);
        verifyNoInteractions(countries);
    }
}
