package com.employee.organization.controller;

import com.employee.organization.dto.request.LocationRequest;
import com.employee.organization.dto.response.LocationResponse;
import com.employee.organization.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization/locations")
@Tag(
        name = "Locations",
        description = "APIs for managing organization work locations"
)
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(
            summary = "Create location",
            description = "Creates a new organization work location"
    )
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody LocationRequest request
    ) {

        LocationResponse response =
                locationService.createLocation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all locations",
            description = "Returns all organization locations"
    )
    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {

        return ResponseEntity.ok(
                locationService.getAllLocations()
        );
    }

    @Operation(
            summary = "Get location by ID",
            description = "Returns a location using its unique identifier"
    )
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                locationService.getLocationById(id)
        );
    }

    @Operation(
            summary = "Get active locations",
            description = "Returns only currently active organization locations"
    )
    @GetMapping("/active")
    public ResponseEntity<List<LocationResponse>> getActiveLocations() {

        return ResponseEntity.ok(
                locationService.getActiveLocations()
        );
    }

    @Operation(
            summary = "Search locations",
            description = "Searches locations by city or country"
    )
    @GetMapping("/search")
    public ResponseEntity<List<LocationResponse>> searchLocations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country
    ) {

        if (city != null && !city.isBlank()) {
            return ResponseEntity.ok(
                    locationService.getLocationsByCity(city)
            );
        }

        if (country != null && !country.isBlank()) {
            return ResponseEntity.ok(
                    locationService.getLocationsByCountry(country)
            );
        }

        return ResponseEntity.ok(
                locationService.getAllLocations()
        );
    }

    @Operation(
            summary = "Update location",
            description = "Updates an existing organization location"
    )
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest request
    ) {

        return ResponseEntity.ok(
                locationService.updateLocation(id, request)
        );
    }

    @Operation(
            summary = "Delete location",
            description = "Deletes an organization location using its unique identifier"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Long id
    ) {

        locationService.deleteLocation(id);

        return ResponseEntity.noContent().build();
    }
}