package com.acme.salary.repository;
import com.acme.salary.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface DepartmentRepository extends JpaRepository<Department,UUID> {
 List<Department> findByActiveTrueOrderByName();
 java.util.Optional<Department> findByNameIgnoreCaseAndActiveTrue(String name);
}
