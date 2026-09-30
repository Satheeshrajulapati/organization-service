package com.employee.organization.service.impl;

import com.employee.organization.dto.request.EmploymentTypeRequest;
import com.employee.organization.dto.response.EmploymentTypeResponse;
import com.employee.organization.entity.EmploymentType;
import com.employee.organization.exception.DuplicateEmploymentTypeException;
import com.employee.organization.exception.EmploymentTypeNotFoundException;
import com.employee.organization.mapper.EmploymentTypeMapper;
import com.employee.organization.repository.EmploymentTypeRepository;
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
class EmploymentTypeServiceImplTest {

    @Mock
    private EmploymentTypeRepository employmentTypeRepository;

    @Mock
    private EmploymentTypeMapper employmentTypeMapper;

    @InjectMocks
    private EmploymentTypeServiceImpl employmentTypeService;

    private EmploymentType employmentType;

    @BeforeEach
    void setUp() {

        employmentType = new EmploymentType(
                1L,
                "FULL_TIME",
                "Full Time",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateEmploymentTypeSuccessfully() {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        " full_time ",
                        " Full Time ",
                        true
                );

        EmploymentTypeResponse response =
                createResponse(employmentType);

        when(employmentTypeRepository
                .existsByCodeIgnoreCase("FULL_TIME"))
                .thenReturn(false);

        when(employmentTypeRepository
                .existsByNameIgnoreCase("Full Time"))
                .thenReturn(false);

        when(employmentTypeMapper
                .toEntity(any(EmploymentTypeRequest.class)))
                .thenReturn(employmentType);

        when(employmentTypeRepository.save(employmentType))
                .thenReturn(employmentType);

        when(employmentTypeMapper.toResponse(employmentType))
                .thenReturn(response);

        EmploymentTypeResponse result =
                employmentTypeService.createEmploymentType(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("FULL_TIME", result.code());
        assertEquals("Full Time", result.name());

        verify(employmentTypeMapper).toEntity(
                argThat(normalized ->
                        normalized.code().equals("FULL_TIME")
                                && normalized.name().equals("Full Time")
                )
        );

        verify(employmentTypeRepository)
                .save(employmentType);
    }

    @Test
    void shouldThrowExceptionWhenCodeAlreadyExists() {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        "FULL_TIME",
                        "Full Time",
                        true
                );

        when(employmentTypeRepository
                .existsByCodeIgnoreCase("FULL_TIME"))
                .thenReturn(true);

        DuplicateEmploymentTypeException exception =
                assertThrows(
                        DuplicateEmploymentTypeException.class,
                        () -> employmentTypeService
                                .createEmploymentType(request)
                );

        assertEquals(
                "Employment type code already exists: FULL_TIME",
                exception.getMessage()
        );

        verify(employmentTypeRepository, never())
                .save(any(EmploymentType.class));
    }

    @Test
    void shouldThrowExceptionWhenNameAlreadyExists() {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        "PERMANENT",
                        "Full Time",
                        true
                );

        when(employmentTypeRepository
                .existsByCodeIgnoreCase("PERMANENT"))
                .thenReturn(false);

        when(employmentTypeRepository
                .existsByNameIgnoreCase("Full Time"))
                .thenReturn(true);

        DuplicateEmploymentTypeException exception =
                assertThrows(
                        DuplicateEmploymentTypeException.class,
                        () -> employmentTypeService
                                .createEmploymentType(request)
                );

        assertEquals(
                "Employment type name already exists: Full Time",
                exception.getMessage()
        );

        verify(employmentTypeRepository, never())
                .save(any(EmploymentType.class));
    }

    @Test
    void shouldReturnEmploymentTypeWhenIdExists() {

        when(employmentTypeRepository.findById(1L))
                .thenReturn(Optional.of(employmentType));

        when(employmentTypeMapper.toResponse(employmentType))
                .thenReturn(createResponse(employmentType));

        EmploymentTypeResponse result =
                employmentTypeService.getEmploymentTypeById(1L);

        assertEquals(1L, result.id());
        assertEquals("FULL_TIME", result.code());
    }

    @Test
    void shouldThrowExceptionWhenEmploymentTypeNotFound() {

        when(employmentTypeRepository.findById(999L))
                .thenReturn(Optional.empty());

        EmploymentTypeNotFoundException exception =
                assertThrows(
                        EmploymentTypeNotFoundException.class,
                        () -> employmentTypeService
                                .getEmploymentTypeById(999L)
                );

        assertEquals(
                "Employment type not found with id: 999",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnAllEmploymentTypes() {

        EmploymentType contract =
                new EmploymentType(
                        2L,
                        "CONTRACT",
                        "Contract",
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(employmentTypeRepository.findAll())
                .thenReturn(List.of(employmentType, contract));

        when(employmentTypeMapper.toResponse(employmentType))
                .thenReturn(createResponse(employmentType));

        when(employmentTypeMapper.toResponse(contract))
                .thenReturn(createResponse(contract));

        List<EmploymentTypeResponse> result =
                employmentTypeService.getAllEmploymentTypes();

        assertEquals(2, result.size());
        assertEquals("FULL_TIME", result.get(0).code());
        assertEquals("CONTRACT", result.get(1).code());
    }

    @Test
    void shouldReturnActiveEmploymentTypes() {

        when(employmentTypeRepository
                .findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(employmentType));

        when(employmentTypeMapper.toResponse(employmentType))
                .thenReturn(createResponse(employmentType));

        List<EmploymentTypeResponse> result =
                employmentTypeService.getActiveEmploymentTypes();

        assertEquals(1, result.size());
        assertTrue(result.get(0).active());
    }

    @Test
    void shouldUpdateEmploymentTypeSuccessfully() {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        " contract ",
                        " Contract ",
                        true
                );

        EmploymentTypeResponse response =
                new EmploymentTypeResponse(
                        1L,
                        "CONTRACT",
                        "Contract",
                        true,
                        employmentType.getCreatedAt(),
                        employmentType.getUpdatedAt()
                );

        when(employmentTypeRepository.findById(1L))
                .thenReturn(Optional.of(employmentType));

        when(employmentTypeRepository
                .existsByCodeIgnoreCaseAndIdNot(
                        "CONTRACT",
                        1L
                ))
                .thenReturn(false);

        when(employmentTypeRepository
                .existsByNameIgnoreCaseAndIdNot(
                        "Contract",
                        1L
                ))
                .thenReturn(false);

        when(employmentTypeRepository.save(employmentType))
                .thenReturn(employmentType);

        when(employmentTypeMapper.toResponse(employmentType))
                .thenReturn(response);

        EmploymentTypeResponse result =
                employmentTypeService.updateEmploymentType(
                        1L,
                        request
                );

        assertEquals("CONTRACT", result.code());
        assertEquals("Contract", result.name());

        verify(employmentTypeMapper).updateEntity(
                argThat(normalized ->
                        normalized.code().equals("CONTRACT")
                                && normalized.name().equals("Contract")
                ),
                eq(employmentType)
        );

        verify(employmentTypeRepository)
                .save(employmentType);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateCode() {

        EmploymentTypeRequest request =
                new EmploymentTypeRequest(
                        "CONTRACT",
                        "Contract",
                        true
                );

        when(employmentTypeRepository.findById(1L))
                .thenReturn(Optional.of(employmentType));

        when(employmentTypeRepository
                .existsByCodeIgnoreCaseAndIdNot(
                        "CONTRACT",
                        1L
                ))
                .thenReturn(true);

        assertThrows(
                DuplicateEmploymentTypeException.class,
                () -> employmentTypeService
                        .updateEmploymentType(1L, request)
        );

        verify(employmentTypeRepository, never())
                .save(any(EmploymentType.class));
    }

    @Test
    void shouldDeleteEmploymentTypeSuccessfully() {

        when(employmentTypeRepository.findById(1L))
                .thenReturn(Optional.of(employmentType));

        employmentTypeService.deleteEmploymentType(1L);

        verify(employmentTypeRepository)
                .delete(employmentType);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingEmploymentType() {

        when(employmentTypeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EmploymentTypeNotFoundException.class,
                () -> employmentTypeService
                        .deleteEmploymentType(999L)
        );

        verify(employmentTypeRepository, never())
                .delete(any(EmploymentType.class));
    }

    private EmploymentTypeResponse createResponse(
            EmploymentType employmentType
    ) {

        return new EmploymentTypeResponse(
                employmentType.getId(),
                employmentType.getCode(),
                employmentType.getName(),
                employmentType.getActive(),
                employmentType.getCreatedAt(),
                employmentType.getUpdatedAt()
        );
    }
}