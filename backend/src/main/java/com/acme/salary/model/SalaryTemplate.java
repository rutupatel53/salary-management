package com.acme.salary.model;

import com.acme.salary.dto.TemplateComponent;
import com.acme.salary.enums.TemplateStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "salary_templates")
public class SalaryTemplate {
    @Id
    @Column(name = "template_id")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TemplateStatus status;

    @Version
    @Column(nullable = false)
    private long revision;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<TemplateComponent> components;

    protected SalaryTemplate() {
    }

    public SalaryTemplate(UUID id) {
        this.id = id;
    }

    public void revise(
            String name,
            String countryCode,
            String currencyCode,
            TemplateStatus status,
            List<TemplateComponent> components) {
        this.name = name.trim();
        this.countryCode = countryCode;
        this.currencyCode = currencyCode;
        this.status = status;
        this.components = List.copyOf(components);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCountryCode() { return countryCode; }
    public String getCurrencyCode() { return currencyCode; }
    public TemplateStatus getStatus() { return status; }
    public long getRevision() { return revision; }
    public List<TemplateComponent> getComponents() { return components; }
}
