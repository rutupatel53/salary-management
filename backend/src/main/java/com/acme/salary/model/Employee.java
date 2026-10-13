package com.acme.salary.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "employees")
public class Employee {
    @Id @Column(name = "employee_id") private UUID id;
    @Column(name = "employee_number", nullable = false) private String employeeNumber;
    @Column(name = "first_name", nullable = false) private String firstName;
    @Column(name = "last_name", nullable = false) private String lastName;
    @Column(name = "work_email", nullable = false) private String workEmail;
    @Column(name = "hire_date", nullable = false) private LocalDate hireDate;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_code", nullable = false) private Country country;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id", nullable = false) private Department department;
    @Column(name = "job_title", nullable = false) private String jobTitle;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "created_at", insertable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false) private Instant updatedAt;

    protected Employee() { }
    public Employee(UUID id, String employeeNumber, String firstName, String lastName, String workEmail,
                    LocalDate hireDate, Country country, Department department, String jobTitle) {
        this.id=id; this.employeeNumber=employeeNumber; this.firstName=firstName; this.lastName=lastName;
        this.workEmail=workEmail; this.hireDate=hireDate; this.country=country; this.department=department; this.jobTitle=jobTitle;
    }
    public void update(String firstName, String lastName, String workEmail, LocalDate hireDate,
                       Country country, Department department, String jobTitle) {
        this.firstName=firstName; this.lastName=lastName; this.workEmail=workEmail;
        this.hireDate=hireDate; this.country=country; this.department=department; this.jobTitle=jobTitle;
    }
    public void archive() { if (archivedAt == null) archivedAt = Instant.now(); }
    public UUID getId(){return id;} public String getEmployeeNumber(){return employeeNumber;}
    public String getFirstName(){return firstName;} public String getLastName(){return lastName;}
    public String getWorkEmail(){return workEmail;} public LocalDate getHireDate(){return hireDate;}
    public Country getCountry(){return country;} public Department getDepartment(){return department;}
    public String getJobTitle(){return jobTitle;} public Instant getArchivedAt(){return archivedAt;}
}
