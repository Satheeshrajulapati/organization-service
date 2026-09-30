package com.employee.organization.service;

import com.employee.organization.dto.request.EmploymentTypeRequest;
import com.employee.organization.dto.response.EmploymentTypeResponse;

import java.util.List;

public interface EmploymentTypeService {

    EmploymentTypeResponse createEmploymentType(
            EmploymentTypeRequest request
    );

    EmploymentTypeResponse getEmploymentTypeById(Long id);

    List<EmploymentTypeResponse> getAllEmploymentTypes();

    List<EmploymentTypeResponse> getActiveEmploymentTypes();

    EmploymentTypeResponse updateEmploymentType(
            Long id,
            EmploymentTypeRequest request
    );

    void deleteEmploymentType(Long id);
}