package com.employee.organization.service.impl;

import com.employee.organization.dto.request.EmploymentTypeRequest;
import com.employee.organization.dto.response.EmploymentTypeResponse;
import com.employee.organization.entity.EmploymentType;
import com.employee.organization.exception.DuplicateEmploymentTypeException;
import com.employee.organization.exception.EmploymentTypeNotFoundException;
import com.employee.organization.mapper.EmploymentTypeMapper;
import com.employee.organization.repository.EmploymentTypeRepository;
import com.employee.organization.service.EmploymentTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmploymentTypeServiceImpl
        implements EmploymentTypeService {

    private final EmploymentTypeRepository employmentTypeRepository;
    private final EmploymentTypeMapper employmentTypeMapper;

    public EmploymentTypeServiceImpl(
            EmploymentTypeRepository employmentTypeRepository,
            EmploymentTypeMapper employmentTypeMapper
    ) {
        this.employmentTypeRepository = employmentTypeRepository;
        this.employmentTypeMapper = employmentTypeMapper;
    }

    @Override
    @Transactional
    public EmploymentTypeResponse createEmploymentType(
            EmploymentTypeRequest request
    ) {

        EmploymentTypeRequest normalizedRequest =
                normalizeRequest(request);

        validateDuplicateForCreate(normalizedRequest);

        EmploymentType employmentType =
                employmentTypeMapper.toEntity(normalizedRequest);

        EmploymentType savedEmploymentType =
                employmentTypeRepository.save(employmentType);

        return employmentTypeMapper.toResponse(savedEmploymentType);
    }

    @Override
    public EmploymentTypeResponse getEmploymentTypeById(Long id) {

        EmploymentType employmentType =
                findEmploymentTypeById(id);

        return employmentTypeMapper.toResponse(employmentType);
    }

    @Override
    public List<EmploymentTypeResponse> getAllEmploymentTypes() {

        return employmentTypeRepository.findAll()
                .stream()
                .map(employmentTypeMapper::toResponse)
                .toList();
    }

    @Override
    public List<EmploymentTypeResponse> getActiveEmploymentTypes() {

        return employmentTypeRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(employmentTypeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public EmploymentTypeResponse updateEmploymentType(
            Long id,
            EmploymentTypeRequest request
    ) {

        EmploymentType employmentType =
                findEmploymentTypeById(id);

        EmploymentTypeRequest normalizedRequest =
                normalizeRequest(request);

        validateDuplicateForUpdate(
                id,
                normalizedRequest
        );

        employmentTypeMapper.updateEntity(
                normalizedRequest,
                employmentType
        );

        EmploymentType updatedEmploymentType =
                employmentTypeRepository.save(employmentType);

        return employmentTypeMapper.toResponse(
                updatedEmploymentType
        );
    }

    @Override
    @Transactional
    public void deleteEmploymentType(Long id) {

        EmploymentType employmentType =
                findEmploymentTypeById(id);

        employmentTypeRepository.delete(employmentType);
    }

    private EmploymentType findEmploymentTypeById(Long id) {

        return employmentTypeRepository
                .findById(id)
                .orElseThrow(
                        () -> new EmploymentTypeNotFoundException(id)
                );
    }

    private void validateDuplicateForCreate(
            EmploymentTypeRequest request
    ) {

        if (employmentTypeRepository
                .existsByCodeIgnoreCase(request.code())) {

            throw new DuplicateEmploymentTypeException(
                    "Employment type code already exists: "
                            + request.code()
            );
        }

        if (employmentTypeRepository
                .existsByNameIgnoreCase(request.name())) {

            throw new DuplicateEmploymentTypeException(
                    "Employment type name already exists: "
                            + request.name()
            );
        }
    }

    private void validateDuplicateForUpdate(
            Long id,
            EmploymentTypeRequest request
    ) {

        if (employmentTypeRepository
                .existsByCodeIgnoreCaseAndIdNot(
                        request.code(),
                        id
                )) {

            throw new DuplicateEmploymentTypeException(
                    "Employment type code already exists: "
                            + request.code()
            );
        }

        if (employmentTypeRepository
                .existsByNameIgnoreCaseAndIdNot(
                        request.name(),
                        id
                )) {

            throw new DuplicateEmploymentTypeException(
                    "Employment type name already exists: "
                            + request.name()
            );
        }
    }

    private EmploymentTypeRequest normalizeRequest(
            EmploymentTypeRequest request
    ) {

        return new EmploymentTypeRequest(
                normalizeCode(request.code()),
                normalizeText(request.name()),
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