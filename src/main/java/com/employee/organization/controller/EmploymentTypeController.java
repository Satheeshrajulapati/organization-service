package com.employee.organization.controller;

import com.employee.organization.dto.request.EmploymentTypeRequest;
import com.employee.organization.dto.response.EmploymentTypeResponse;
import com.employee.organization.service.EmploymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization/employment-types")
@Tag(
        name = "Employment Types",
        description = "APIs for managing organization employment types"
)
public class EmploymentTypeController {

    private final EmploymentTypeService employmentTypeService;

    public EmploymentTypeController(
            EmploymentTypeService employmentTypeService
    ) {
        this.employmentTypeService = employmentTypeService;
    }

    @Operation(
            summary = "Create employment type",
            description = "Creates a new employment type"
    )
    @PostMapping
    public ResponseEntity<EmploymentTypeResponse> createEmploymentType(
            @Valid @RequestBody EmploymentTypeRequest request
    ) {

        EmploymentTypeResponse response =
                employmentTypeService.createEmploymentType(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all employment types",
            description = "Returns all employment types including active and inactive records"
    )
    @GetMapping
    public ResponseEntity<List<EmploymentTypeResponse>>
    getAllEmploymentTypes() {

        return ResponseEntity.ok(
                employmentTypeService.getAllEmploymentTypes()
        );
    }

    @Operation(
            summary = "Get employment type by ID",
            description = "Returns an employment type using its unique identifier"
    )
    @GetMapping("/{id}")
    public ResponseEntity<EmploymentTypeResponse>
    getEmploymentTypeById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                employmentTypeService.getEmploymentTypeById(id)
        );
    }

    @Operation(
            summary = "Get active employment types",
            description = "Returns only currently active employment types"
    )
    @GetMapping("/active")
    public ResponseEntity<List<EmploymentTypeResponse>>
    getActiveEmploymentTypes() {

        return ResponseEntity.ok(
                employmentTypeService.getActiveEmploymentTypes()
        );
    }

    @Operation(
            summary = "Update employment type",
            description = "Updates an existing employment type using its unique identifier"
    )
    @PutMapping("/{id}")
    public ResponseEntity<EmploymentTypeResponse> updateEmploymentType(
            @PathVariable Long id,
            @Valid @RequestBody EmploymentTypeRequest request
    ) {

        return ResponseEntity.ok(
                employmentTypeService.updateEmploymentType(
                        id,
                        request
                )
        );
    }

    @Operation(
            summary = "Delete employment type",
            description = "Deletes an employment type using its unique identifier"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmploymentType(
            @PathVariable Long id
    ) {

        employmentTypeService.deleteEmploymentType(id);

        return ResponseEntity.noContent().build();
    }
}