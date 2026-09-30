package com.employee.organization.service;

import com.employee.organization.dto.request.DepartmentRequest;
import com.employee.organization.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse getDepartmentById(Long id);

    List<DepartmentResponse> getAllDepartments();

    List<DepartmentResponse> getActiveDepartments();

    DepartmentResponse updateDepartment(
            Long id,
            DepartmentRequest request
    );

    void deleteDepartment(Long id);
}