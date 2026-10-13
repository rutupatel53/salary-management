package com.acme.salary.controller;

import com.acme.salary.dto.ActivityResponse;
import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ActivityResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String action) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be zero or greater and page size must be 1 to 100.");
        }
        return ApiResponse.success(HttpStatus.OK.value(), activityService.list(page, size, action),
                "Activity retrieved.");
    }
}
