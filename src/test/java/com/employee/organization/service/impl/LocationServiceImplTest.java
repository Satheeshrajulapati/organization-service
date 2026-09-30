package com.employee.organization.service.impl;

import com.employee.organization.dto.request.LocationRequest;
import com.employee.organization.dto.response.LocationResponse;
import com.employee.organization.entity.Location;
import com.employee.organization.exception.DuplicateLocationException;
import com.employee.organization.exception.LocationNotFoundException;
import com.employee.organization.mapper.LocationMapper;
import com.employee.organization.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationServiceImpl locationService;

    private Location location;

    @BeforeEach
    void setUp() {

        location = new Location(
                1L,
                "HYD01",
                "Hyderabad Development Center",
                "Hyderabad",
                "Telangana",
                "India",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateLocationSuccessfully() {

        LocationRequest request = new LocationRequest(
                " hyd01 ",
                " Hyderabad Development Center ",
                " Hyderabad ",
                " Telangana ",
                " India ",
                true
        );

        LocationResponse response = createResponse(location);

        when(locationRepository.existsByCodeIgnoreCase("HYD01"))
                .thenReturn(false);

        when(locationMapper.toEntity(any(LocationRequest.class)))
                .thenReturn(location);

        when(locationRepository.save(location))
                .thenReturn(location);

        when(locationMapper.toResponse(location))
                .thenReturn(response);

        LocationResponse result =
                locationService.createLocation(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("HYD01", result.code());
        assertEquals("Hyderabad", result.city());

        verify(locationMapper).toEntity(
                argThat(normalized ->
                        normalized.code().equals("HYD01")
                                && normalized.name().equals(
                                "Hyderabad Development Center")
                                && normalized.city().equals("Hyderabad")
                                && normalized.state().equals("Telangana")
                                && normalized.country().equals("India")
                )
        );

        verify(locationRepository).save(location);
    }

    @Test
    void shouldThrowExceptionWhenLocationCodeAlreadyExists() {

        LocationRequest request = new LocationRequest(
                "HYD01",
                "Another Hyderabad Office",
                "Hyderabad",
                "Telangana",
                "India",
                true
        );

        when(locationRepository.existsByCodeIgnoreCase("HYD01"))
                .thenReturn(true);

        DuplicateLocationException exception =
                assertThrows(
                        DuplicateLocationException.class,
                        () -> locationService.createLocation(request)
                );

        assertEquals(
                "Location code already exists: HYD01",
                exception.getMessage()
        );

        verify(locationRepository, never())
                .save(any(Location.class));
    }

    @Test
    void shouldReturnLocationWhenIdExists() {

        LocationResponse response = createResponse(location);

        when(locationRepository.findById(1L))
                .thenReturn(Optional.of(location));

        when(locationMapper.toResponse(location))
                .thenReturn(response);

        LocationResponse result =
                locationService.getLocationById(1L);

        assertEquals(1L, result.id());
        assertEquals("HYD01", result.code());
    }

    @Test
    void shouldThrowExceptionWhenLocationNotFound() {

        when(locationRepository.findById(999L))
                .thenReturn(Optional.empty());

        LocationNotFoundException exception =
                assertThrows(
                        LocationNotFoundException.class,
                        () -> locationService.getLocationById(999L)
                );

        assertEquals(
                "Location not found with id: 999",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnAllLocations() {

        Location secondLocation = new Location(
                2L,
                "BLR01",
                "Bengaluru Development Center",
                "Bengaluru",
                "Karnataka",
                "India",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(locationRepository.findAll())
                .thenReturn(List.of(location, secondLocation));

        when(locationMapper.toResponse(location))
                .thenReturn(createResponse(location));

        when(locationMapper.toResponse(secondLocation))
                .thenReturn(createResponse(secondLocation));

        List<LocationResponse> result =
                locationService.getAllLocations();

        assertEquals(2, result.size());
        assertEquals("HYD01", result.get(0).code());
        assertEquals("BLR01", result.get(1).code());
    }

    @Test
    void shouldReturnActiveLocations() {

        when(locationRepository
                .findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(location));

        when(locationMapper.toResponse(location))
                .thenReturn(createResponse(location));

        List<LocationResponse> result =
                locationService.getActiveLocations();

        assertEquals(1, result.size());
        assertTrue(result.get(0).active());
    }

    @Test
    void shouldReturnLocationsByCity() {

        when(locationRepository
                .findAllByCityIgnoreCaseOrderByNameAsc("Hyderabad"))
                .thenReturn(List.of(location));

        when(locationMapper.toResponse(location))
                .thenReturn(createResponse(location));

        List<LocationResponse> result =
                locationService.getLocationsByCity(" Hyderabad ");

        assertEquals(1, result.size());
        assertEquals("Hyderabad", result.get(0).city());

        verify(locationRepository)
                .findAllByCityIgnoreCaseOrderByNameAsc("Hyderabad");
    }

    @Test
    void shouldReturnLocationsByCountry() {

        when(locationRepository
                .findAllByCountryIgnoreCaseOrderByNameAsc("India"))
                .thenReturn(List.of(location));

        when(locationMapper.toResponse(location))
                .thenReturn(createResponse(location));

        List<LocationResponse> result =
                locationService.getLocationsByCountry(" India ");

        assertEquals(1, result.size());
        assertEquals("India", result.get(0).country());

        verify(locationRepository)
                .findAllByCountryIgnoreCaseOrderByNameAsc("India");
    }

    @Test
    void shouldUpdateLocationSuccessfully() {

        LocationRequest request = new LocationRequest(
                " hyd02 ",
                " Hyderabad Corporate Office ",
                " Hyderabad ",
                " Telangana ",
                " India ",
                true
        );

        LocationResponse response = new LocationResponse(
                1L,
                "HYD02",
                "Hyderabad Corporate Office",
                "Hyderabad",
                "Telangana",
                "India",
                true,
                location.getCreatedAt(),
                location.getUpdatedAt()
        );

        when(locationRepository.findById(1L))
                .thenReturn(Optional.of(location));

        when(locationRepository
                .existsByCodeIgnoreCaseAndIdNot("HYD02", 1L))
                .thenReturn(false);

        when(locationRepository.save(location))
                .thenReturn(location);

        when(locationMapper.toResponse(location))
                .thenReturn(response);

        LocationResponse result =
                locationService.updateLocation(1L, request);

        assertEquals("HYD02", result.code());
        assertEquals(
                "Hyderabad Corporate Office",
                result.name()
        );

        verify(locationMapper).updateEntity(
                argThat(normalized ->
                        normalized.code().equals("HYD02")
                                && normalized.name().equals(
                                "Hyderabad Corporate Office")
                ),
                eq(location)
        );

        verify(locationRepository).save(location);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateCode() {

        LocationRequest request = new LocationRequest(
                "BLR01",
                "Bengaluru Office",
                "Bengaluru",
                "Karnataka",
                "India",
                true
        );

        when(locationRepository.findById(1L))
                .thenReturn(Optional.of(location));

        when(locationRepository
                .existsByCodeIgnoreCaseAndIdNot("BLR01", 1L))
                .thenReturn(true);

        assertThrows(
                DuplicateLocationException.class,
                () -> locationService.updateLocation(1L, request)
        );

        verify(locationRepository, never())
                .save(any(Location.class));
    }

    @Test
    void shouldDeleteLocationSuccessfully() {

        when(locationRepository.findById(1L))
                .thenReturn(Optional.of(location));

        locationService.deleteLocation(1L);

        verify(locationRepository).delete(location);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingLocation() {

        when(locationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                LocationNotFoundException.class,
                () -> locationService.deleteLocation(999L)
        );

        verify(locationRepository, never())
                .delete(any(Location.class));
    }

    private LocationResponse createResponse(Location location) {

        return new LocationResponse(
                location.getId(),
                location.getCode(),
                location.getName(),
                location.getCity(),
                location.getState(),
                location.getCountry(),
                location.getActive(),
                location.getCreatedAt(),
                location.getUpdatedAt()
        );
    }
}