package com.employee.organization.service;

import com.employee.organization.dto.request.LocationRequest;
import com.employee.organization.dto.response.LocationResponse;

import java.util.List;

public interface LocationService {

    LocationResponse createLocation(LocationRequest request);

    LocationResponse getLocationById(Long id);

    List<LocationResponse> getAllLocations();

    List<LocationResponse> getActiveLocations();

    List<LocationResponse> getLocationsByCity(String city);

    List<LocationResponse> getLocationsByCountry(String country);

    LocationResponse updateLocation(
            Long id,
            LocationRequest request
    );

    void deleteLocation(Long id);
}