package com.employee.organization.service;

import com.employee.organization.dto.request.DesignationRequest;
import com.employee.organization.dto.response.DesignationResponse;

import java.util.List;

public interface DesignationService {

    DesignationResponse createDesignation(DesignationRequest request);

    DesignationResponse getDesignationById(Long id);

    List<DesignationResponse> getAllDesignations();

    List<DesignationResponse> getActiveDesignations();

    DesignationResponse updateDesignation(
            Long id,
            DesignationRequest request
    );

    void deleteDesignation(Long id);
}