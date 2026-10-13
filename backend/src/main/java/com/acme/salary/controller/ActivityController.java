package com.acme.salary.controller;

import com.acme.salary.dto.ActivityResponse;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.service.ActivityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityService service;
    public ActivityController(ActivityService service) { this.service = service; }

    @GetMapping
    public PageResponse<ActivityResponse> list(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "25") int size,
                                               @RequestParam(required = false) String action) {
        return service.list(page, size, action);
    }
}
