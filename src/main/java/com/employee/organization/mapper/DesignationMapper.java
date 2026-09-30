package com.employee.organization.mapper;

import com.employee.organization.dto.request.DesignationRequest;
import com.employee.organization.dto.response.DesignationResponse;
import com.employee.organization.entity.Designation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DesignationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Designation toEntity(DesignationRequest request);

    DesignationResponse toResponse(Designation designation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            DesignationRequest request,
            @MappingTarget Designation designation
    );
}