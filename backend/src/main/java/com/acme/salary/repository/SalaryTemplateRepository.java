package com.acme.salary.repository;

import com.acme.salary.enums.TemplateStatus;
import com.acme.salary.model.SalaryTemplate;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SalaryTemplateRepository extends JpaRepository<SalaryTemplate, UUID> {
    @Query("select template from SalaryTemplate template "
            + "where (:country is null or template.countryCode = :country) "
            + "and (:status is null or template.status = :status) "
            + "and lower(template.name) like lower(concat('%', :query, '%'))")
    Page<SalaryTemplate> search(
            @Param("query") String query,
            @Param("country") String country,
            @Param("status") TemplateStatus status,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select template from SalaryTemplate template where template.id = :id")
    Optional<SalaryTemplate> findForAssignment(@Param("id") UUID id);
}
