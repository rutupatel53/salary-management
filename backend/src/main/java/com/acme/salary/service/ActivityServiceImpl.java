package com.acme.salary.service;

import com.acme.salary.dto.ActivityResponse;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.repository.ActivityLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityLogRepository repository;
    public ActivityServiceImpl(ActivityLogRepository repository) { this.repository = repository; }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityResponse> list(int page, int size, String action) {
        if (page < 0) throw new IllegalArgumentException("Page must be zero or greater.");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Page size must be between 1 and 100.");
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("occurredAt"), Sort.Order.desc("id")));
        var records = action == null || action.isBlank() ? repository.findAll(pageable) : repository.findByAction(action, pageable);
        return new PageResponse<>(records.getContent().stream().map(a -> new ActivityResponse(a.getId(), a.getOccurredAt(), a.getActorIdentifier(), a.getAction(), a.getEmployeeId(), a.getDetails())).toList(),
                records.getNumber(), records.getSize(), records.getTotalElements(), records.getTotalPages());
    }
}
