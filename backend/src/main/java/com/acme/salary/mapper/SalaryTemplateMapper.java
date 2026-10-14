package com.acme.salary.mapper;

import com.acme.salary.model.SalaryTemplate;
import com.acme.salary.dto.SalaryTemplateResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface SalaryTemplateMapper {
    SalaryTemplateResponse toResponse(SalaryTemplate template);
}
