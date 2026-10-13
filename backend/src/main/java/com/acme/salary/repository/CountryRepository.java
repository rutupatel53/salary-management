package com.acme.salary.repository;
import com.acme.salary.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CountryRepository extends JpaRepository<Country,String> { }
