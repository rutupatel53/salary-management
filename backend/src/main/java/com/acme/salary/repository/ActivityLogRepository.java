package com.acme.salary.repository;
import com.acme.salary.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {
 Page<ActivityLog> findByAction(String action, Pageable pageable);
}
