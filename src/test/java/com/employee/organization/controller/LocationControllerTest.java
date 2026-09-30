package com.employee.organization.controller;

import com.employee.organization.dto.request.LocationRequest;
import com.employee.organization.dto.response.LocationResponse;
import com.employee.organization.exception.DuplicateLocationException;
import com.employee.organization.exception.GlobalExceptionHandler;
import com.employee.organization.exception.LocationNotFoundException;
import com.employee.organization.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LocationControllerTest {

    private MockMvc mockMvc;

    private LocationService locationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        locationService = mock(LocationService.class);

        LocationController locationController =
                new LocationController(locationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(locationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldCreateLocationSuccessfully() throws Exception {

        LocationRequest request = new LocationRequest(
                "HYD01",
                "Hyderabad Development Center",
                "Hyderabad",
                "Telangana",
                "India",
                true
        );

        LocationResponse response = createResponse();

        when(locationService.createLocation(
                any(LocationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/organization/locations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("HYD01"))
                .andExpect(jsonPath("$.name")
                        .value("Hyderabad Development Center"))
                .andExpect(jsonPath("$.city")
                        .value("Hyderabad"))
                .andExpect(jsonPath("$.state")
                        .value("Telangana"))
                .andExpect(jsonPath("$.country")
                        .value("India"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnBadRequestForInvalidLocation()
            throws Exception {

        String request = """
                {
                    "code": "",
                    "name": "",
                    "city": "",
                    "state": "",
                    "country": "",
                    "active": null
                }
                """;

        mockMvc.perform(
                        post("/api/organization/locations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.code")
                        .value("Location code is required"))
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Location name is required"))
                .andExpect(jsonPath("$.validationErrors.city")
                        .value("City is required"))
                .andExpect(jsonPath("$.validationErrors.state")
                        .value("State is required"))
                .andExpect(jsonPath("$.validationErrors.country")
                        .value("Country is required"))
                .andExpect(jsonPath("$.validationErrors.active")
                        .value("Location active status is required"));

        verifyNoInteractions(locationService);
    }

    @Test
    void shouldReturnConflictForDuplicateLocation()
            throws Exception {

        LocationRequest request = new LocationRequest(
                "HYD01",
                "Hyderabad Development Center",
                "Hyderabad",
                "Telangana",
                "India",
                true
        );

        when(locationService.createLocation(
                any(LocationRequest.class)))
                .thenThrow(
                        new DuplicateLocationException(
                                "Location code already exists: HYD01"
                        )
                );

        mockMvc.perform(
                        post("/api/organization/locations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Location code already exists: HYD01"
                        ));
    }

    @Test
    void shouldReturnLocationById() throws Exception {

        when(locationService.getLocationById(1L))
                .thenReturn(createResponse());

        mockMvc.perform(
                        get(
                                "/api/organization/locations/{id}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code")
                        .value("HYD01"))
                .andExpect(jsonPath("$.city")
                        .value("Hyderabad"));
    }

    @Test
    void shouldReturnNotFoundWhenLocationDoesNotExist()
            throws Exception {

        when(locationService.getLocationById(999L))
                .thenThrow(
                        new LocationNotFoundException(999L)
                );

        mockMvc.perform(
                        get(
                                "/api/organization/locations/{id}",
                                999L
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Location not found with id: 999"));
    }

    @Test
    void shouldReturnAllLocations() throws Exception {

        LocationResponse hyd = createResponse();

        LocationResponse blr = new LocationResponse(
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

        when(locationService.getAllLocations())
                .thenReturn(List.of(hyd, blr));

        mockMvc.perform(
                        get("/api/organization/locations")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code")
                        .value("HYD01"))
                .andExpect(jsonPath("$[1].code")
                        .value("BLR01"));
    }

    @Test
    void shouldReturnActiveLocations() throws Exception {

        when(locationService.getActiveLocations())
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                        get("/api/organization/locations/active")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].active")
                        .value(true));

        verify(locationService).getActiveLocations();
    }

    @Test
    void shouldSearchLocationsByCity() throws Exception {

        when(locationService.getLocationsByCity("Hyderabad"))
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                        get("/api/organization/locations/search")
                                .param("city", "Hyderabad")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].city")
                        .value("Hyderabad"));

        verify(locationService)
                .getLocationsByCity("Hyderabad");

        verify(locationService, never())
                .getLocationsByCountry(anyString());
    }

    @Test
    void shouldSearchLocationsByCountry() throws Exception {

        when(locationService.getLocationsByCountry("India"))
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                        get("/api/organization/locations/search")
                                .param("country", "India")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].country")
                        .value("India"));

        verify(locationService)
                .getLocationsByCountry("India");
    }

    @Test
    void shouldReturnAllLocationsWhenSearchHasNoFilters()
            throws Exception {

        when(locationService.getAllLocations())
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                        get("/api/organization/locations/search")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(locationService).getAllLocations();

        verify(locationService, never())
                .getLocationsByCity(anyString());

        verify(locationService, never())
                .getLocationsByCountry(anyString());
    }

    @Test
    void shouldUpdateLocationSuccessfully()
            throws Exception {

        LocationRequest request = new LocationRequest(
                "HYD02",
                "Hyderabad Corporate Office",
                "Hyderabad",
                "Telangana",
                "India",
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
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(locationService.updateLocation(
                eq(1L),
                any(LocationRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put(
                                "/api/organization/locations/{id}",
                                1L
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code")
                        .value("HYD02"))
                .andExpect(jsonPath("$.name")
                        .value("Hyderabad Corporate Office"));
    }

    @Test
    void shouldDeleteLocationSuccessfully()
            throws Exception {

        doNothing()
                .when(locationService)
                .deleteLocation(1L);

        mockMvc.perform(
                        delete(
                                "/api/organization/locations/{id}",
                                1L
                        )
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(locationService).deleteLocation(1L);
    }

    private LocationResponse createResponse() {

        return new LocationResponse(
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
}