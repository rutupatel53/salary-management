package com.acme.salary.service;

import com.acme.salary.dto.ActivityResponse;
import com.acme.salary.dto.PageResponse;

public interface ActivityService {
    PageResponse<ActivityResponse> list(int page, int size, String action);
}
