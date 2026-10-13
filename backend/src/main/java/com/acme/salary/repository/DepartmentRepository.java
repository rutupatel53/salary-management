package com.acme.salary.repository;
import com.acme.salary.dto.DepartmentResponse;
import org.springframework.data.jpa.repository.Query;
import com.acme.salary.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByActiveTrueOrderByName();

    Optional<Department> findByNameIgnoreCaseAndActiveTrue(String name);

    Optional<Department> findByIdAndActiveTrue(UUID id);
    @Query("select new com.acme.salary.dto.DepartmentResponse(d.id, d.name) from Department d where d.active = true order by d.name")
    List<DepartmentResponse> findActiveDepartmentOptions();
}
