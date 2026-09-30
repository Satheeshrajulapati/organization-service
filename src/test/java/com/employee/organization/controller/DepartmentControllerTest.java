package com.employee.organization.controller;

import com.employee.organization.dto.request.DepartmentRequest;
import com.employee.organization.dto.response.DepartmentResponse;
import com.employee.organization.exception.DepartmentNotFoundException;
import com.employee.organization.exception.DuplicateDepartmentException;
import com.employee.organization.exception.GlobalExceptionHandler;
import com.employee.organization.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DepartmentControllerTest {

    private MockMvc mockMvc;

    private DepartmentService departmentService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        departmentService = mock(DepartmentService.class);

        DepartmentController departmentController =
                new DepartmentController(departmentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(departmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldCreateDepartmentSuccessfully() throws Exception {

        DepartmentRequest request =
                new DepartmentRequest(
                        "IT",
                        "Information Technology",
                        true
                );

        DepartmentResponse response =
                new DepartmentResponse(
                        1L,
                        "IT",
                        "Information Technology",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(departmentService.createDepartment(any(DepartmentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/organization/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("IT"))
                .andExpect(jsonPath("$.name")
                        .value("Information Technology"))
                .andExpect(jsonPath("$.active").value(true));

        verify(departmentService)
                .createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidDepartment() throws Exception {

        String request = """
                {
                    "code": "",
                    "name": "",
                    "active": null
                }
                """;

        mockMvc.perform(
                        post("/api/organization/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.code")
                        .value("Department code is required"))
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Department name is required"))
                .andExpect(jsonPath("$.validationErrors.active")
                        .value("Department active status is required"));

        verifyNoInteractions(departmentService);
    }

    @Test
    void shouldReturnConflictForDuplicateDepartment() throws Exception {

        DepartmentRequest request =
                new DepartmentRequest(
                        "IT",
                        "Information Technology",
                        true
                );

        when(departmentService.createDepartment(any(DepartmentRequest.class)))
                .thenThrow(
                        new DuplicateDepartmentException(
                                "Department code already exists: IT"
                        )
                );

        mockMvc.perform(
                        post("/api/organization/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Department code already exists: IT"));
    }

    @Test
    void shouldReturnDepartmentById() throws Exception {

        DepartmentResponse response =
                new DepartmentResponse(
                        1L,
                        "IT",
                        "Information Technology",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(departmentService.getDepartmentById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/organization/departments/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("IT"))
                .andExpect(jsonPath("$.name")
                        .value("Information Technology"));
    }

    @Test
    void shouldReturnNotFoundWhenDepartmentDoesNotExist()
            throws Exception {

        when(departmentService.getDepartmentById(999L))
                .thenThrow(new DepartmentNotFoundException(999L));

        mockMvc.perform(
                        get("/api/organization/departments/{id}", 999L)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Department not found with id: 999"));
    }

    @Test
    void shouldReturnAllDepartments() throws Exception {

        DepartmentResponse it =
                new DepartmentResponse(
                        1L,
                        "IT",
                        "Information Technology",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        DepartmentResponse hr =
                new DepartmentResponse(
                        2L,
                        "HR",
                        "Human Resources",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(departmentService.getAllDepartments())
                .thenReturn(List.of(it, hr));

        mockMvc.perform(
                        get("/api/organization/departments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("IT"))
                .andExpect(jsonPath("$[1].code").value("HR"));
    }

    @Test
    void shouldDeleteDepartmentSuccessfully() throws Exception {

        doNothing()
                .when(departmentService)
                .deleteDepartment(1L);

        mockMvc.perform(
                        delete("/api/organization/departments/{id}", 1L)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(departmentService)
                .deleteDepartment(1L);
    }
}