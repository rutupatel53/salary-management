package com.acme.salary.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity @Table(name = "activity_log")
public class ActivityLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "activity_id") private Long id;
    @Column(name = "occurred_at", insertable = false, updatable = false) private Instant occurredAt;
    @Column(name = "actor_identifier", nullable = false) private String actorIdentifier;
    @Column(nullable = false) private String action;
    @Column(name = "employee_id") private UUID employeeId;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb", nullable = false) private Map<String,Object> details;
    protected ActivityLog() { }
    public ActivityLog(String actorIdentifier, String action, UUID employeeId, Map<String,Object> details) {
        this.actorIdentifier=actorIdentifier; this.action=action; this.employeeId=employeeId; this.details=details;
    }
    public Long getId(){return id;} public Instant getOccurredAt(){return occurredAt;}
    public String getActorIdentifier(){return actorIdentifier;} public String getAction(){return action;}
    public UUID getEmployeeId(){return employeeId;} public Map<String,Object> getDetails(){return details;}
}
