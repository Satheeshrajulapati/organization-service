package com.employee.organization.controller;

import com.employee.organization.dto.request.DesignationRequest;
import com.employee.organization.dto.response.DesignationResponse;
import com.employee.organization.exception.DesignationNotFoundException;
import com.employee.organization.exception.DuplicateDesignationException;
import com.employee.organization.exception.GlobalExceptionHandler;
import com.employee.organization.service.DesignationService;
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

class DesignationControllerTest {

    private MockMvc mockMvc;

    private DesignationService designationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        designationService = mock(DesignationService.class);

        DesignationController designationController =
                new DesignationController(designationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(designationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldCreateDesignationSuccessfully() throws Exception {

        DesignationRequest request =
                new DesignationRequest(
                        "SE",
                        "Software Engineer",
                        true
                );

        DesignationResponse response =
                new DesignationResponse(
                        1L,
                        "SE",
                        "Software Engineer",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(designationService
                .createDesignation(any(DesignationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/organization/designations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("SE"))
                .andExpect(jsonPath("$.name")
                        .value("Software Engineer"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnBadRequestForInvalidDesignation()
            throws Exception {

        String request = """
                {
                    "code": "",
                    "name": "",
                    "active": null
                }
                """;

        mockMvc.perform(
                        post("/api/organization/designations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.code")
                        .value("Designation code is required"))
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Designation name is required"))
                .andExpect(jsonPath("$.validationErrors.active")
                        .value("Designation active status is required"));

        verifyNoInteractions(designationService);
    }

    @Test
    void shouldReturnConflictForDuplicateDesignation()
            throws Exception {

        DesignationRequest request =
                new DesignationRequest(
                        "SE",
                        "Software Engineer",
                        true
                );

        when(designationService
                .createDesignation(any(DesignationRequest.class)))
                .thenThrow(
                        new DuplicateDesignationException(
                                "Designation code already exists: SE"
                        )
                );

        mockMvc.perform(
                        post("/api/organization/designations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Designation code already exists: SE"));
    }

    @Test
    void shouldReturnDesignationById() throws Exception {

        DesignationResponse response =
                new DesignationResponse(
                        1L,
                        "SE",
                        "Software Engineer",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(designationService.getDesignationById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/organization/designations/{id}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("SE"));
    }

    @Test
    void shouldReturnNotFoundWhenDesignationDoesNotExist()
            throws Exception {

        when(designationService.getDesignationById(999L))
                .thenThrow(
                        new DesignationNotFoundException(999L)
                );

        mockMvc.perform(
                        get(
                                "/api/organization/designations/{id}",
                                999L
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Designation not found with id: 999"));
    }

    @Test
    void shouldReturnAllDesignations() throws Exception {

        DesignationResponse se =
                new DesignationResponse(
                        1L,
                        "SE",
                        "Software Engineer",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        DesignationResponse tl =
                new DesignationResponse(
                        2L,
                        "TL",
                        "Technical Lead",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(designationService.getAllDesignations())
                .thenReturn(List.of(se, tl));

        mockMvc.perform(
                        get("/api/organization/designations")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("SE"))
                .andExpect(jsonPath("$[1].code").value("TL"));
    }

    @Test
    void shouldDeleteDesignationSuccessfully()
            throws Exception {

        doNothing()
                .when(designationService)
                .deleteDesignation(1L);

        mockMvc.perform(
                        delete(
                                "/api/organization/designations/{id}",
                                1L
                        )
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(designationService)
                .deleteDesignation(1L);
    }
}