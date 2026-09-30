package com.employee.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(

        @NotBlank(message = "Department code is required")
        @Size(max = 20, message = "Department code must not exceed 20 characters")
        String code,

        @NotBlank(message = "Department name is required")
        @Size(max = 100, message = "Department name must not exceed 100 characters")
        String name,

        @NotNull(message = "Department active status is required")
        Boolean active

) {
}