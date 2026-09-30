package com.employee.organization.mapper;

import com.employee.organization.dto.request.EmploymentTypeRequest;
import com.employee.organization.dto.response.EmploymentTypeResponse;
import com.employee.organization.entity.EmploymentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmploymentTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EmploymentType toEntity(EmploymentTypeRequest request);

    EmploymentTypeResponse toResponse(
            EmploymentType employmentType
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            EmploymentTypeRequest request,
            @MappingTarget EmploymentType employmentType
    );
}