package com.acme.salary.dto;

import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Map;

/** Public sort fields mapped to trusted JPA paths. Shared by listing and export. */
public record EmployeeSort(String sortBy, String direction) {
    private static final Map<String, List<String>> FIELDS = Map.of(
            "employeeNumber", List.of("employeeNumber"),
            "name", List.of("firstName", "lastName"),
            "workEmail", List.of("workEmail"),
            "hireDate", List.of("hireDate"),
            "country", List.of("country.name"),
            "department", List.of("department.name"),
            "jobTitle", List.of("jobTitle"));

    public EmployeeSort {
        if (!FIELDS.containsKey(sortBy)) {
            throw new IllegalArgumentException("Unsupported employee sort field.");
        }
        if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
            throw new IllegalArgumentException("Sort direction must be asc or desc.");
        }
    }

    public Sort toSort() {
        Sort.Direction order = Sort.Direction.fromString(direction);
        List<Sort.Order> orders = FIELDS.get(sortBy).stream()
                .map(property -> new Sort.Order(order, property)).toList();
        return Sort.by(orders).and(Sort.by("id").ascending());
    }
}
