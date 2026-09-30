package com.employee.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DesignationRequest(

        @NotBlank(message = "Designation code is required")
        @Size(
                max = 20,
                message = "Designation code must not exceed 20 characters"
        )
        String code,

        @NotBlank(message = "Designation name is required")
        @Size(
                max = 100,
                message = "Designation name must not exceed 100 characters"
        )
        String name,

        @NotNull(message = "Designation active status is required")
        Boolean active

) {
}