package com.employee.organization.service.impl;

import com.employee.organization.dto.request.DesignationRequest;
import com.employee.organization.dto.response.DesignationResponse;
import com.employee.organization.entity.Designation;
import com.employee.organization.exception.DesignationNotFoundException;
import com.employee.organization.exception.DuplicateDesignationException;
import com.employee.organization.mapper.DesignationMapper;
import com.employee.organization.repository.DesignationRepository;
import com.employee.organization.service.DesignationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final DesignationMapper designationMapper;

    public DesignationServiceImpl(
            DesignationRepository designationRepository,
            DesignationMapper designationMapper
    ) {
        this.designationRepository = designationRepository;
        this.designationMapper = designationMapper;
    }

    @Override
    @Transactional
    public DesignationResponse createDesignation(
            DesignationRequest request
    ) {

        String code = normalizeCode(request.code());
        String name = normalizeName(request.name());

        validateDuplicatesForCreate(code, name);

        DesignationRequest normalizedRequest =
                new DesignationRequest(
                        code,
                        name,
                        request.active()
                );

        Designation designation =
                designationMapper.toEntity(normalizedRequest);

        Designation savedDesignation =
                designationRepository.save(designation);

        return designationMapper.toResponse(savedDesignation);
    }

    @Override
    public DesignationResponse getDesignationById(Long id) {

        Designation designation = findDesignationById(id);

        return designationMapper.toResponse(designation);
    }

    @Override
    public List<DesignationResponse> getAllDesignations() {

        return designationRepository.findAll()
                .stream()
                .map(designationMapper::toResponse)
                .toList();
    }

    @Override
    public List<DesignationResponse> getActiveDesignations() {

        return designationRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(designationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DesignationResponse updateDesignation(
            Long id,
            DesignationRequest request
    ) {

        Designation designation = findDesignationById(id);

        String code = normalizeCode(request.code());
        String name = normalizeName(request.name());

        validateDuplicatesForUpdate(
                id,
                code,
                name
        );

        DesignationRequest normalizedRequest =
                new DesignationRequest(
                        code,
                        name,
                        request.active()
                );

        designationMapper.updateEntity(
                normalizedRequest,
                designation
        );

        Designation updatedDesignation =
                designationRepository.save(designation);

        return designationMapper.toResponse(updatedDesignation);
    }

    @Override
    @Transactional
    public void deleteDesignation(Long id) {

        Designation designation = findDesignationById(id);

        designationRepository.delete(designation);
    }

    private Designation findDesignationById(Long id) {

        return designationRepository.findById(id)
                .orElseThrow(
                        () -> new DesignationNotFoundException(id)
                );
    }

    private void validateDuplicatesForCreate(
            String code,
            String name
    ) {

        if (designationRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateDesignationException(
                    "Designation code already exists: " + code
            );
        }

        if (designationRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateDesignationException(
                    "Designation name already exists: " + name
            );
        }
    }

    private void validateDuplicatesForUpdate(
            Long id,
            String code,
            String name
    ) {

        if (designationRepository
                .existsByCodeIgnoreCaseAndIdNot(code, id)) {

            throw new DuplicateDesignationException(
                    "Designation code already exists: " + code
            );
        }

        if (designationRepository
                .existsByNameIgnoreCaseAndIdNot(name, id)) {

            throw new DuplicateDesignationException(
                    "Designation name already exists: " + name
            );
        }
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}