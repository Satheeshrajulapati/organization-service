package com.employee.organization.service.impl;

import com.employee.organization.dto.request.DepartmentRequest;
import com.employee.organization.dto.response.DepartmentResponse;
import com.employee.organization.entity.Department;
import com.employee.organization.exception.DepartmentNotFoundException;
import com.employee.organization.exception.DuplicateDepartmentException;
import com.employee.organization.mapper.DepartmentMapper;
import com.employee.organization.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;

    @BeforeEach
    void setUp() {

        department = new Department(
                1L,
                "IT",
                "Information Technology",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateDepartmentSuccessfully() {

        DepartmentRequest request =
                new DepartmentRequest(
                        " it ",
                        " Information Technology ",
                        true
                );

        DepartmentResponse response =
                new DepartmentResponse(
                        1L,
                        "IT",
                        "Information Technology",
                        true,
                        department.getCreatedAt(),
                        department.getUpdatedAt()
                );

        when(departmentRepository.existsByCodeIgnoreCase("IT"))
                .thenReturn(false);

        when(departmentRepository
                .existsByNameIgnoreCase("Information Technology"))
                .thenReturn(false);

        when(departmentMapper.toEntity(any(DepartmentRequest.class)))
                .thenReturn(department);

        when(departmentRepository.save(department))
                .thenReturn(department);

        when(departmentMapper.toResponse(department))
                .thenReturn(response);

        DepartmentResponse result =
                departmentService.createDepartment(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("IT", result.code());
        assertEquals("Information Technology", result.name());

        verify(departmentRepository)
                .existsByCodeIgnoreCase("IT");

        verify(departmentRepository)
                .existsByNameIgnoreCase("Information Technology");

        verify(departmentRepository)
                .save(department);
    }

    @Test
    void shouldThrowExceptionWhenDepartmentCodeAlreadyExists() {

        DepartmentRequest request =
                new DepartmentRequest(
                        "IT",
                        "Information Technology",
                        true
                );

        when(departmentRepository.existsByCodeIgnoreCase("IT"))
                .thenReturn(true);

        DuplicateDepartmentException exception =
                assertThrows(
                        DuplicateDepartmentException.class,
                        () -> departmentService.createDepartment(request)
                );

        assertEquals(
                "Department code already exists: IT",
                exception.getMessage()
        );

        verify(departmentRepository, never())
                .save(any(Department.class));
    }

    @Test
    void shouldReturnDepartmentWhenIdExists() {

        DepartmentResponse response =
                new DepartmentResponse(
                        1L,
                        "IT",
                        "Information Technology",
                        true,
                        department.getCreatedAt(),
                        department.getUpdatedAt()
                );

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(departmentMapper.toResponse(department))
                .thenReturn(response);

        DepartmentResponse result =
                departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("IT", result.code());
    }

    @Test
    void shouldThrowExceptionWhenDepartmentNotFound() {

        when(departmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        DepartmentNotFoundException exception =
                assertThrows(
                        DepartmentNotFoundException.class,
                        () -> departmentService.getDepartmentById(999L)
                );

        assertEquals(
                "Department not found with id: 999",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenDepartmentNameAlreadyExists() {

        DepartmentRequest request =
                new DepartmentRequest(
                        "TECH",
                        "Information Technology",
                        true
                );

        when(departmentRepository.existsByCodeIgnoreCase("TECH"))
                .thenReturn(false);

        when(departmentRepository
                .existsByNameIgnoreCase("Information Technology"))
                .thenReturn(true);

        DuplicateDepartmentException exception =
                assertThrows(
                        DuplicateDepartmentException.class,
                        () -> departmentService.createDepartment(request)
                );

        assertEquals(
                "Department name already exists: Information Technology",
                exception.getMessage()
        );

        verify(departmentRepository, never())
                .save(any(Department.class));
    }


    @Test
    void shouldUpdateDepartmentSuccessfully() {

        DepartmentRequest request =
                new DepartmentRequest(
                        " hr ",
                        " Human Resources ",
                        true
                );

        DepartmentResponse response =
                new DepartmentResponse(
                        1L,
                        "HR",
                        "Human Resources",
                        true,
                        department.getCreatedAt(),
                        department.getUpdatedAt()
                );

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(departmentRepository
                .existsByCodeIgnoreCaseAndIdNot("HR", 1L))
                .thenReturn(false);

        when(departmentRepository
                .existsByNameIgnoreCaseAndIdNot("Human Resources", 1L))
                .thenReturn(false);

        when(departmentRepository.save(department))
                .thenReturn(department);

        when(departmentMapper.toResponse(department))
                .thenReturn(response);

        DepartmentResponse result =
                departmentService.updateDepartment(1L, request);

        assertNotNull(result);
        assertEquals("HR", result.code());
        assertEquals("Human Resources", result.name());

        verify(departmentMapper).updateEntity(
                argThat(normalizedRequest ->
                        normalizedRequest.code().equals("HR")
                                && normalizedRequest.name()
                                .equals("Human Resources")
                ),
                eq(department)
        );

        verify(departmentRepository).save(department);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateCode() {

        DepartmentRequest request =
                new DepartmentRequest(
                        "HR",
                        "Human Resources",
                        true
                );

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(departmentRepository
                .existsByCodeIgnoreCaseAndIdNot("HR", 1L))
                .thenReturn(true);

        DuplicateDepartmentException exception =
                assertThrows(
                        DuplicateDepartmentException.class,
                        () -> departmentService.updateDepartment(1L, request)
                );

        assertEquals(
                "Department code already exists: HR",
                exception.getMessage()
        );

        verify(departmentRepository, never())
                .save(any(Department.class));
    }


    @Test
    void shouldDeleteDepartmentSuccessfully() {

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository)
                .delete(department);
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingDepartment() {

        when(departmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                DepartmentNotFoundException.class,
                () -> departmentService.deleteDepartment(999L)
        );

        verify(departmentRepository, never())
                .delete(any(Department.class));
    }
}