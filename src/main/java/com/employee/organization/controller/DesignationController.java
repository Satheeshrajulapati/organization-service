package com.employee.organization.controller;

import com.employee.organization.dto.request.DesignationRequest;
import com.employee.organization.dto.response.DesignationResponse;
import com.employee.organization.service.DesignationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization/designations")
@Tag(
        name = "Designations",
        description = "APIs for managing organization designations"
)
public class DesignationController {

    private final DesignationService designationService;

    public DesignationController(
            DesignationService designationService
    ) {
        this.designationService = designationService;
    }

    @Operation(
            summary = "Create designation",
            description = "Creates a new designation in the organization"
    )
    @PostMapping
    public ResponseEntity<DesignationResponse> createDesignation(
            @Valid @RequestBody DesignationRequest request
    ) {

        DesignationResponse response =
                designationService.createDesignation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all designations",
            description = "Returns all designations including active and inactive designations"
    )
    @GetMapping
    public ResponseEntity<List<DesignationResponse>> getAllDesignations() {

        return ResponseEntity.ok(
                designationService.getAllDesignations()
        );
    }

    @Operation(
            summary = "Get designation by ID",
            description = "Returns a designation using its unique identifier"
    )
    @GetMapping("/{id}")
    public ResponseEntity<DesignationResponse> getDesignationById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                designationService.getDesignationById(id)
        );
    }

    @Operation(
            summary = "Get active designations",
            description = "Returns only designations that are currently active"
    )
    @GetMapping("/active")
    public ResponseEntity<List<DesignationResponse>> getActiveDesignations() {

        return ResponseEntity.ok(
                designationService.getActiveDesignations()
        );
    }

    @Operation(
            summary = "Update designation",
            description = "Updates an existing designation using its unique identifier"
    )
    @PutMapping("/{id}")
    public ResponseEntity<DesignationResponse> updateDesignation(
            @PathVariable Long id,
            @Valid @RequestBody DesignationRequest request
    ) {

        return ResponseEntity.ok(
                designationService.updateDesignation(id, request)
        );
    }

    @Operation(
            summary = "Delete designation",
            description = "Deletes an existing designation using its unique identifier"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(
            @PathVariable Long id
    ) {

        designationService.deleteDesignation(id);

        return ResponseEntity.noContent().build();
    }
}