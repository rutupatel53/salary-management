package com.acme.salary.audit;

import com.acme.salary.model.ActivityLog;
import com.acme.salary.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {
    private final ActivityLogRepository activityLogRepository;

    public AuditService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void record(String action, UUID employeeId, Map<String, Object> details) {
        ActivityLog activity = new ActivityLog("assessment-hr-manager", action, employeeId, details);
        activityLogRepository.save(activity);
    }
}
