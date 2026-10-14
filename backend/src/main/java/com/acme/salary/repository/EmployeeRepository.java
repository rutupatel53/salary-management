package com.acme.salary.repository;

import com.acme.salary.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {

    @Override
    @EntityGraph(attributePaths = {"country", "department"})
    Page<Employee> findAll(Specification<Employee> specification, Pageable pageable);

    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);

    boolean existsByWorkEmailIgnoreCase(String workEmail);

    boolean existsByWorkEmailIgnoreCaseAndIdNot(String workEmail, UUID id);

    Optional<Employee> findByIdAndArchivedAtIsNull(UUID id);

    @Query(value = "SELECT lower(employee_number) FROM employees WHERE lower(employee_number) IN (:values)",
            nativeQuery = true)
    Set<String> findExistingEmployeeNumbers(Collection<String> values);

    @Query(value = "SELECT lower(work_email) FROM employees WHERE lower(work_email) IN (:values)",
            nativeQuery = true)
    Set<String> findExistingWorkEmails(Collection<String> values);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Employee e where e.id = :id and e.archivedAt is null")
    Optional<Employee> findActiveForSalaryUpdate(@Param("id") UUID id);
}
