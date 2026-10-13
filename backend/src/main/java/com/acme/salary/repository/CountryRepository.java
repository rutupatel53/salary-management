package com.acme.salary.repository;
import com.acme.salary.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {
    List<Country> findAllByOrderByName();
}
