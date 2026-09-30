package com.employee.organization.dto.response;

import java.time.LocalDateTime;

public record LocationResponse(
        Long id,
        String code,
        String name,
        String city,
        String state,
        String country,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}