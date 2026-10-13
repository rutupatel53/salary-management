package com.acme.salary.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ActivityResponse(long id, Instant occurredAt, String actor, String action, UUID employeeId, Map<String, Object> details) { }
