package com.employee.organization.service.impl;

import com.employee.organization.dto.request.DesignationRequest;
import com.employee.organization.dto.response.DesignationResponse;
import com.employee.organization.entity.Designation;
import com.employee.organization.exception.DesignationNotFoundException;
import com.employee.organization.exception.DuplicateDesignationException;
import com.employee.organization.mapper.DesignationMapper;
import com.employee.organization.repository.DesignationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesignationServiceImplTest {

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private DesignationMapper designationMapper;

    @InjectMocks
    private DesignationServiceImpl designationService;

    private Designation designation;

    @BeforeEach
    void setUp() {

        designation = new Designation(
                1L,
                "SE",
                "Software Engineer",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateDesignationSuccessfully() {

        DesignationRequest request =
                new DesignationRequest(
                        " se ",
                        " Software Engineer ",
                        true
                );

        DesignationResponse response =
                new DesignationResponse(
                        1L,
                        "SE",
                        "Software Engineer",
                        true,
                        designation.getCreatedAt(),
                        designation.getUpdatedAt()
                );

        when(designationRepository.existsByCodeIgnoreCase("SE"))
                .thenReturn(false);

        when(designationRepository
                .existsByNameIgnoreCase("Software Engineer"))
                .thenReturn(false);

        when(designationMapper.toEntity(any(DesignationRequest.class)))
                .thenReturn(designation);

        when(designationRepository.save(designation))
                .thenReturn(designation);

        when(designationMapper.toResponse(designation))
                .thenReturn(response);

        DesignationResponse result =
                designationService.createDesignation(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("SE", result.code());
        assertEquals("Software Engineer", result.name());

        verify(designationMapper).toEntity(
                argThat(normalizedRequest ->
                        normalizedRequest.code().equals("SE")
                                && normalizedRequest.name()
                                .equals("Software Engineer")
                )
        );

        verify(designationRepository).save(designation);
    }

    @Test
    void shouldThrowExceptionWhenDesignationCodeAlreadyExists() {

        DesignationRequest request =
                new DesignationRequest(
                        "SE",
                        "Software Engineer",
                        true
                );

        when(designationRepository.existsByCodeIgnoreCase("SE"))
                .thenReturn(true);

        DuplicateDesignationException exception =
                assertThrows(
                        DuplicateDesignationException.class,
                        () -> designationService.createDesignation(request)
                );

        assertEquals(
                "Designation code already exists: SE",
                exception.getMessage()
        );

        verify(designationRepository, never())
                .save(any(Designation.class));
    }

    @Test
    void shouldThrowExceptionWhenDesignationNameAlreadyExists() {

        DesignationRequest request =
                new DesignationRequest(
                        "DEV",
                        "Software Engineer",
                        true
                );

        when(designationRepository.existsByCodeIgnoreCase("DEV"))
                .thenReturn(false);

        when(designationRepository
                .existsByNameIgnoreCase("Software Engineer"))
                .thenReturn(true);

        DuplicateDesignationException exception =
                assertThrows(
                        DuplicateDesignationException.class,
                        () -> designationService.createDesignation(request)
                );

        assertEquals(
                "Designation name already exists: Software Engineer",
                exception.getMessage()
        );

        verify(designationRepository, never())
                .save(any(Designation.class));
    }

    @Test
    void shouldReturnDesignationWhenIdExists() {

        DesignationResponse response =
                new DesignationResponse(
                        1L,
                        "SE",
                        "Software Engineer",
                        true,
                        designation.getCreatedAt(),
                        designation.getUpdatedAt()
                );

        when(designationRepository.findById(1L))
                .thenReturn(Optional.of(designation));

        when(designationMapper.toResponse(designation))
                .thenReturn(response);

        DesignationResponse result =
                designationService.getDesignationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("SE", result.code());
    }

    @Test
    void shouldThrowExceptionWhenDesignationNotFound() {

        when(designationRepository.findById(999L))
                .thenReturn(Optional.empty());

        DesignationNotFoundException exception =
                assertThrows(
                        DesignationNotFoundException.class,
                        () -> designationService.getDesignationById(999L)
                );

        assertEquals(
                "Designation not found with id: 999",
                exception.getMessage()
        );
    }

    @Test
    void shouldUpdateDesignationSuccessfully() {

        DesignationRequest request =
                new DesignationRequest(
                        " sse ",
                        " Senior Software Engineer ",
                        true
                );

        DesignationResponse response =
                new DesignationResponse(
                        1L,
                        "SSE",
                        "Senior Software Engineer",
                        true,
                        designation.getCreatedAt(),
                        designation.getUpdatedAt()
                );

        when(designationRepository.findById(1L))
                .thenReturn(Optional.of(designation));

        when(designationRepository
                .existsByCodeIgnoreCaseAndIdNot("SSE", 1L))
                .thenReturn(false);

        when(designationRepository
                .existsByNameIgnoreCaseAndIdNot(
                        "Senior Software Engineer",
                        1L
                ))
                .thenReturn(false);

        when(designationRepository.save(designation))
                .thenReturn(designation);

        when(designationMapper.toResponse(designation))
                .thenReturn(response);

        DesignationResponse result =
                designationService.updateDesignation(1L, request);

        assertEquals("SSE", result.code());
        assertEquals("Senior Software Engineer", result.name());

        verify(designationMapper).updateEntity(
                argThat(normalizedRequest ->
                        normalizedRequest.code().equals("SSE")
                                && normalizedRequest.name()
                                .equals("Senior Software Engineer")
                ),
                eq(designation)
        );

        verify(designationRepository).save(designation);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateCode() {

        DesignationRequest request =
                new DesignationRequest(
                        "TL",
                        "Technical Lead",
                        true
                );

        when(designationRepository.findById(1L))
                .thenReturn(Optional.of(designation));

        when(designationRepository
                .existsByCodeIgnoreCaseAndIdNot("TL", 1L))
                .thenReturn(true);

        assertThrows(
                DuplicateDesignationException.class,
                () -> designationService.updateDesignation(1L, request)
        );

        verify(designationRepository, never())
                .save(any(Designation.class));
    }

    @Test
    void shouldDeleteDesignationSuccessfully() {

        when(designationRepository.findById(1L))
                .thenReturn(Optional.of(designation));

        designationService.deleteDesignation(1L);

        verify(designationRepository).delete(designation);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingDesignation() {

        when(designationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                DesignationNotFoundException.class,
                () -> designationService.deleteDesignation(999L)
        );

        verify(designationRepository, never())
                .delete(any(Designation.class));
    }
}