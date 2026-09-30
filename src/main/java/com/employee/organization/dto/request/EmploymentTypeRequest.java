package com.employee.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmploymentTypeRequest(

        @NotBlank(message = "Employment type code is required")
        @Size(
                max = 30,
                message = "Employment type code must not exceed 30 characters"
        )
        String code,

        @NotBlank(message = "Employment type name is required")
        @Size(
                max = 100,
                message = "Employment type name must not exceed 100 characters"
        )
        String name,

        @NotNull(message = "Employment type active status is required")
        Boolean active

) {
}