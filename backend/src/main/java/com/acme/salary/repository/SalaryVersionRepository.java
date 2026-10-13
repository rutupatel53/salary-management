package com.acme.salary.repository;

import com.acme.salary.model.SalaryVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalaryVersionRepository extends JpaRepository<SalaryVersion, UUID> {
    List<SalaryVersion> findByEmployeeIdOrderByEffectiveDateDesc(UUID employeeId);

    Optional<SalaryVersion> findTopByEmployeeIdOrderByEffectiveDateDesc(UUID employeeId);
}
