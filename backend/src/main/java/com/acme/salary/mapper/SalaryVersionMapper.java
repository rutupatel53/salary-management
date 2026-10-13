package com.acme.salary.mapper;
import com.acme.salary.dto.SalaryVersionResponse;
import com.acme.salary.model.SalaryVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel="spring")
public interface SalaryVersionMapper{
 @Mapping(target="basic",source="basic")
 SalaryVersionResponse toResponse(SalaryVersion salaryVersion);
}
