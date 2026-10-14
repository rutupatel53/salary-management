package com.acme.salary.model;

import com.acme.salary.dto.SalaryBreakdown;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "salary_versions")
public class SalaryVersion {
    @Id
    @Column(name = "salary_version_id")
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "basic_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal basic;

    @Column(name = "allowances_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal allowances;

    @Column(name = "deductions_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal deductions;

    @Column(insertable = false, updatable = false, precision = 14, scale = 2)
    private BigDecimal gross;

    @Column(insertable = false, updatable = false, precision = 14, scale = 2)
    private BigDecimal net;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "template_id", updatable = false)
    private UUID templateId;

    @Column(name = "template_name", updatable = false, length = 100)
    private String templateName;

    @Column(name = "template_revision", updatable = false)
    private Long templateRevision;

    @Column(name = "annual_ctc", updatable = false, precision = 14, scale = 2)
    private BigDecimal annualCtc;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "assignment_breakdown", updatable = false, columnDefinition = "jsonb")
    private SalaryBreakdown assignmentBreakdown;

    protected SalaryVersion() {
    }

    public SalaryVersion(
            UUID id,
            UUID employeeId,
            LocalDate effectiveDate,
            String currencyCode,
            BigDecimal basic,
            BigDecimal allowances,
            BigDecimal deductions) {
        this.id = id;
        this.employeeId = employeeId;
        this.effectiveDate = effectiveDate;
        this.currencyCode = currencyCode;
        this.basic = basic;
        this.allowances = allowances;
        this.deductions = deductions;
    }

    public void setAssignmentSnapshot(
            UUID templateId,
            String templateName,
            long templateRevision,
            BigDecimal annualCtc,
            SalaryBreakdown assignmentBreakdown) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.templateRevision = templateRevision;
        this.annualCtc = annualCtc;
        this.assignmentBreakdown = assignmentBreakdown;
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public String getCurrencyCode() { return currencyCode; }
    public BigDecimal getBasic() { return basic; }
    public BigDecimal getAllowances() { return allowances; }
    public BigDecimal getDeductions() { return deductions; }
    public BigDecimal getGross() { return gross; }
    public BigDecimal getNet() { return net; }
    public Instant getCreatedAt() { return createdAt; }
    public String getTemplateName() { return templateName; }
    public Long getTemplateRevision() { return templateRevision; }
    public BigDecimal getAnnualCtc() { return annualCtc; }
    public SalaryBreakdown getAssignmentBreakdown() { return assignmentBreakdown; }
}
