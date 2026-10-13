package com.acme.salary.repository;
import com.acme.salary.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> { }
