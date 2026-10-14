package com.acme.salary.dto;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeSortTest {
    @Test
    void nameSortUsesBothNamesAndAnIdTieBreaker() {
        Sort sort = new EmployeeSort("name", "desc").toSort();
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("firstName").getDirection());
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("lastName").getDirection());
        assertEquals(Sort.Direction.ASC, sort.getOrderFor("id").getDirection());
    }

    @Test
    void publicCountrySortMapsToTrustedJpaPath() {
        assertNotNull(new EmployeeSort("country", "asc").toSort().getOrderFor("country.name"));
    }

    @Test
    void rejectsUnknownFieldsAndInvalidDirections() {
        assertThrows(IllegalArgumentException.class, () -> new EmployeeSort("archivedAt", "asc"));
        assertThrows(IllegalArgumentException.class, () -> new EmployeeSort("name", "sideways"));
    }
}
