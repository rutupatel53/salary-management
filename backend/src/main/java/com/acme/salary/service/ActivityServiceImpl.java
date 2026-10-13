package com.acme.salary.service;

import com.acme.salary.dto.ActivityResponse;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.model.ActivityLog;
import com.acme.salary.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ActivityServiceImpl implements ActivityService {
    private final ActivityLogRepository activityLogRepository;

    public ActivityServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public PageResponse<ActivityResponse> list(int page, int size, String action) {
        validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("occurredAt"), Sort.Order.desc("id")));
        Page<ActivityLog> records = action == null || action.isBlank()
                ? activityLogRepository.findAll(pageable)
                : activityLogRepository.findByAction(action, pageable);

        return new PageResponse<>(
                records.getContent().stream().map(this::toResponse).toList(),
                records.getNumber(), records.getSize(), records.getTotalElements(), records.getTotalPages());
    }

    private ActivityResponse toResponse(ActivityLog record) {
        return new ActivityResponse(record.getId(), record.getOccurredAt(), record.getActorIdentifier(),
                record.getAction(), record.getEmployeeId(), record.getDetails());
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater.");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100.");
        }
    }
}
