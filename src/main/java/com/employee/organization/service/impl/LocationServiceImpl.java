package com.employee.organization.service.impl;

import com.employee.organization.dto.request.LocationRequest;
import com.employee.organization.dto.response.LocationResponse;
import com.employee.organization.entity.Location;
import com.employee.organization.exception.DuplicateLocationException;
import com.employee.organization.exception.LocationNotFoundException;
import com.employee.organization.mapper.LocationMapper;
import com.employee.organization.repository.LocationRepository;
import com.employee.organization.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationServiceImpl(
            LocationRepository locationRepository,
            LocationMapper locationMapper
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    @Transactional
    public LocationResponse createLocation(LocationRequest request) {

        String code = normalizeCode(request.code());

        validateDuplicateCodeForCreate(code);

        LocationRequest normalizedRequest =
                normalizeRequest(request);

        Location location =
                locationMapper.toEntity(normalizedRequest);

        Location savedLocation =
                locationRepository.save(location);

        return locationMapper.toResponse(savedLocation);
    }

    @Override
    public LocationResponse getLocationById(Long id) {

        Location location = findLocationById(id);

        return locationMapper.toResponse(location);
    }

    @Override
    public List<LocationResponse> getAllLocations() {

        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Override
    public List<LocationResponse> getActiveLocations() {

        return locationRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Override
    public List<LocationResponse> getLocationsByCity(String city) {

        String normalizedCity = normalizeText(city);

        return locationRepository
                .findAllByCityIgnoreCaseOrderByNameAsc(normalizedCity)
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Override
    public List<LocationResponse> getLocationsByCountry(String country) {

        String normalizedCountry = normalizeText(country);

        return locationRepository
                .findAllByCountryIgnoreCaseOrderByNameAsc(normalizedCountry)
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(
            Long id,
            LocationRequest request
    ) {

        Location location = findLocationById(id);

        String code = normalizeCode(request.code());

        validateDuplicateCodeForUpdate(id, code);

        LocationRequest normalizedRequest =
                normalizeRequest(request);

        locationMapper.updateEntity(
                normalizedRequest,
                location
        );

        Location updatedLocation =
                locationRepository.save(location);

        return locationMapper.toResponse(updatedLocation);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {

        Location location = findLocationById(id);

        locationRepository.delete(location);
    }

    private Location findLocationById(Long id) {

        return locationRepository.findById(id)
                .orElseThrow(
                        () -> new LocationNotFoundException(id)
                );
    }

    private void validateDuplicateCodeForCreate(String code) {

        if (locationRepository.existsByCodeIgnoreCase(code)) {

            throw new DuplicateLocationException(
                    "Location code already exists: " + code
            );
        }
    }

    private void validateDuplicateCodeForUpdate(
            Long id,
            String code
    ) {

        if (locationRepository
                .existsByCodeIgnoreCaseAndIdNot(code, id)) {

            throw new DuplicateLocationException(
                    "Location code already exists: " + code
            );
        }
    }

    private LocationRequest normalizeRequest(
            LocationRequest request
    ) {

        return new LocationRequest(
                normalizeCode(request.code()),
                normalizeText(request.name()),
                normalizeText(request.city()),
                normalizeText(request.state()),
                normalizeText(request.country()),
                request.active()
        );
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private String normalizeText(String value) {
        return value.trim();
    }
}