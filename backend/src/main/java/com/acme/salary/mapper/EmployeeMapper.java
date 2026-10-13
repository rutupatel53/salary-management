package com.acme.salary.mapper;
import com.acme.salary.dto.EmployeeResponse;
import com.acme.salary.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface EmployeeMapper {
 @Mapping(target="countryCode", source="country.code")
 @Mapping(target="countryName", source="country.name")
 @Mapping(target="departmentId", source="department.id")
 @Mapping(target="departmentName", source="department.name")
 @Mapping(target="currencyCode", source="country.currencyCode")
 EmployeeResponse toResponse(Employee employee);
}
