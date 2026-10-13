package com.acme.salary.repository;
import com.acme.salary.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;
public interface EmployeeRepository extends JpaRepository<Employee,UUID>, JpaSpecificationExecutor<Employee> {
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
    boolean existsByWorkEmailIgnoreCase(String workEmail);
    boolean existsByWorkEmailIgnoreCaseAndIdNot(String workEmail, UUID id);
}
