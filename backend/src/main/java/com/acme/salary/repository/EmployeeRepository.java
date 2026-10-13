package com.acme.salary.repository;
import com.acme.salary.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.UUID;
public interface EmployeeRepository extends JpaRepository<Employee,UUID>, JpaSpecificationExecutor<Employee> {
    @Override @EntityGraph(attributePaths = {"country", "department"})
    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
    boolean existsByWorkEmailIgnoreCase(String workEmail);
    boolean existsByWorkEmailIgnoreCaseAndIdNot(String workEmail, UUID id);
}
