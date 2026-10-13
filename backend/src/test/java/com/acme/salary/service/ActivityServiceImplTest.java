package com.acme.salary.service;

import com.acme.salary.repository.ActivityLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceImplTest {
    @Test void listIsPagedAndMapsReadOnlyActivityRows() {
        var repository = mock(ActivityLogRepository.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 25), 0));
        var service = new ActivityServiceImpl(repository);
        var result = service.list(0, 25, null);
        assertEquals(0, result.totalItems());
        verify(repository).findAll(argThat((Pageable page) -> page.getPageSize() == 25 && page.getSort().getOrderFor("occurredAt").getDirection() == Sort.Direction.DESC));
    }

    @Test void rejectsUnboundedPagination() {
        var service = new ActivityServiceImpl(mock(ActivityLogRepository.class));
        assertThrows(IllegalArgumentException.class, () -> service.list(0, 101, null));
        assertThrows(IllegalArgumentException.class, () -> service.list(-1, 25, null));
    }
}
