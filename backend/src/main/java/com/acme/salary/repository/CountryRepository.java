package com.acme.salary.repository;
import com.acme.salary.dto.CountryResponse;
import org.springframework.data.jpa.repository.Query;
import com.acme.salary.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {
    List<Country> findAllByOrderByName();
    @Query("select new com.acme.salary.dto.CountryResponse(c.code, c.name, c.currencyCode) from Country c order by c.name")
    List<CountryResponse> findCountryOptions();
}
