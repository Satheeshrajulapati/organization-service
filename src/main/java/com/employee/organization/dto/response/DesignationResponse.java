package com.employee.organization.dto.response;

import java.time.LocalDateTime;

public record DesignationResponse(
        Long id,
        String code,
        String name,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}