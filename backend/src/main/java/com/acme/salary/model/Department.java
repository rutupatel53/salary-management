package com.acme.salary.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity @Table(name = "departments")
public class Department {
    @Id @Column(name = "department_id") private UUID id;
    private String name;
    private boolean active;
    protected Department() { }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
}
