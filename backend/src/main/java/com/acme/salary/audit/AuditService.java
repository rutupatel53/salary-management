package com.acme.salary.audit;
import com.acme.salary.model.ActivityLog;
import com.acme.salary.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {
 private final ActivityLogRepository repository;
 public AuditService(ActivityLogRepository repository){this.repository=repository;}
 public void record(String action, UUID employeeId, Map<String,Object> details){
  repository.save(new ActivityLog("assessment-hr-manager",action,employeeId,details));
 }
}
