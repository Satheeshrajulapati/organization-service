package com.employee.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocationRequest(

        @NotBlank(message = "Location code is required")
        @Size(max = 20, message = "Location code must not exceed 20 characters")
        String code,

        @NotBlank(message = "Location name is required")
        @Size(max = 100, message = "Location name must not exceed 100 characters")
        String name,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        String state,

        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country,

        @NotNull(message = "Location active status is required")
        Boolean active

) {
}