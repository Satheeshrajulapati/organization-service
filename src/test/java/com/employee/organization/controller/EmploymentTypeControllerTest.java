package com.employee.organization.controller;

import com.employee.organization.dto.request.EmploymentTypeRequest;
import com.employee.organization.dto.response.EmploymentTypeResponse;
import com.employee.organization.exception.DuplicateEmploymentTypeException;
import com.employee.organization.exception.EmploymentTypeNotFoundException;
import com.employee.organization.exception.GlobalExceptionHandler;
import com.employee.organization.service.EmploymentTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmploymentTypeControllerTest {

    private MockMvc mockMvc;
    private EmploymentTypeService employmentTypeService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        employmentTypeService =
                mock(EmploymentTypeService.class);

        EmploymentTypeController controller =
                new EmploymentTypeController(
                        employmentTypeService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldCreateEmploymentTypeSuccessfully()
            throws Exception {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        "FULL_TIME",
                        "Full Time",
                        true
                );

        when(employmentTypeService.createEmploymentType(
                any(EmploymentTypeRequest.class)
        )).thenReturn(createResponse());

        mockMvc.perform(
                        post(
                                "/api/organization/employment-types"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code")
                        .value("FULL_TIME"))
                .andExpect(jsonPath("$.name")
                        .value("Full Time"))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }

    @Test
    void shouldReturnBadRequestForInvalidEmploymentType()
            throws Exception {

        String request = """
                {
                    "code": "",
                    "name": "",
                    "active": null
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/organization/employment-types"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath(
                        "$.validationErrors.code"
                ).value(
                        "Employment type code is required"
                ))
                .andExpect(jsonPath(
                        "$.validationErrors.name"
                ).value(
                        "Employment type name is required"
                ))
                .andExpect(jsonPath(
                        "$.validationErrors.active"
                ).value(
                        "Employment type active status is required"
                ));

        verifyNoInteractions(employmentTypeService);
    }

    @Test
    void shouldReturnConflictForDuplicateEmploymentType()
            throws Exception {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        "FULL_TIME",
                        "Full Time",
                        true
                );

        when(employmentTypeService.createEmploymentType(
                any(EmploymentTypeRequest.class)
        )).thenThrow(
                new DuplicateEmploymentTypeException(
                        "Employment type code already exists: FULL_TIME"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/organization/employment-types"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Employment type code already exists: FULL_TIME"
                        ));
    }

    @Test
    void shouldReturnEmploymentTypeById()
            throws Exception {

        when(employmentTypeService
                .getEmploymentTypeById(1L))
                .thenReturn(createResponse());

        mockMvc.perform(
                        get(
                                "/api/organization/employment-types/{id}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code")
                        .value("FULL_TIME"));
    }

    @Test
    void shouldReturnNotFoundWhenEmploymentTypeDoesNotExist()
            throws Exception {

        when(employmentTypeService
                .getEmploymentTypeById(999L))
                .thenThrow(
                        new EmploymentTypeNotFoundException(
                                999L
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/organization/employment-types/{id}",
                                999L
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Employment type not found with id: 999"
                        ));
    }

    @Test
    void shouldReturnAllEmploymentTypes()
            throws Exception {

        EmploymentTypeResponse fullTime =
                createResponse();

        EmploymentTypeResponse contract =
                new EmploymentTypeResponse(
                        2L,
                        "CONTRACT",
                        "Contract",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(employmentTypeService
                .getAllEmploymentTypes())
                .thenReturn(
                        List.of(fullTime, contract)
                );

        mockMvc.perform(
                        get(
                                "/api/organization/employment-types"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].code")
                        .value("FULL_TIME"))
                .andExpect(jsonPath("$[1].code")
                        .value("CONTRACT"));
    }

    @Test
    void shouldReturnActiveEmploymentTypes()
            throws Exception {

        when(employmentTypeService
                .getActiveEmploymentTypes())
                .thenReturn(
                        List.of(createResponse())
                );

        mockMvc.perform(
                        get(
                                "/api/organization/employment-types/active"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].active")
                        .value(true));
    }

    @Test
    void shouldUpdateEmploymentTypeSuccessfully()
            throws Exception {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        "CONTRACT",
                        "Contract",
                        true
                );

        EmploymentTypeResponse response =
                new EmploymentTypeResponse(
                        1L,
                        "CONTRACT",
                        "Contract",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(employmentTypeService.updateEmploymentType(
                eq(1L),
                any(EmploymentTypeRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put(
                                "/api/organization/employment-types/{id}",
                                1L
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value("CONTRACT"))
                .andExpect(jsonPath("$.name")
                        .value("Contract"));
    }

    @Test
    void shouldDeleteEmploymentTypeSuccessfully()
            throws Exception {

        doNothing()
                .when(employmentTypeService)
                .deleteEmploymentType(1L);

        mockMvc.perform(
                        delete(
                                "/api/organization/employment-types/{id}",
                                1L
                        )
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(employmentTypeService)
                .deleteEmploymentType(1L);
    }

    private EmploymentTypeResponse createResponse() {

        return new EmploymentTypeResponse(
                1L,
                "FULL_TIME",
                "Full Time",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}