package com.employee.organization.service.impl;

import com.employee.organization.dto.request.DepartmentRequest;
import com.employee.organization.dto.response.DepartmentResponse;
import com.employee.organization.entity.Department;
import com.employee.organization.exception.DepartmentNotFoundException;
import com.employee.organization.exception.DuplicateDepartmentException;
import com.employee.organization.mapper.DepartmentMapper;
import com.employee.organization.repository.DepartmentRepository;
import com.employee.organization.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository,
            DepartmentMapper departmentMapper
    ) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {

        String code = normalizeCode(request.code());
        String name = normalizeName(request.name());

        validateDuplicatesForCreate(code, name);

        DepartmentRequest normalizedRequest =
                new DepartmentRequest(
                        code,
                        name,
                        request.active()
                );

        Department department =
                departmentMapper.toEntity(normalizedRequest);

        Department savedDepartment =
                departmentRepository.save(department);

        return departmentMapper.toResponse(savedDepartment);
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {

        Department department = findDepartmentById(id);

        return departmentMapper.toResponse(department);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<DepartmentResponse> getActiveDepartments() {

        return departmentRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(
            Long id,
            DepartmentRequest request
    ) {

        Department department = findDepartmentById(id);

        String code = normalizeCode(request.code());
        String name = normalizeName(request.name());

        validateDuplicatesForUpdate(id, code, name);

        DepartmentRequest normalizedRequest =
                new DepartmentRequest(
                        code,
                        name,
                        request.active()
                );

        departmentMapper.updateEntity(
                normalizedRequest,
                department
        );

        Department updatedDepartment =
                departmentRepository.save(department);

        return departmentMapper.toResponse(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {

        Department department = findDepartmentById(id);

        departmentRepository.delete(department);
    }

    private Department findDepartmentById(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(
                        () -> new DepartmentNotFoundException(id)
                );
    }

    private void validateDuplicatesForCreate(
            String code,
            String name
    ) {

        if (departmentRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateDepartmentException(
                    "Department code already exists: " + code
            );
        }

        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateDepartmentException(
                    "Department name already exists: " + name
            );
        }
    }

    private void validateDuplicatesForUpdate(
            Long id,
            String code,
            String name
    ) {

        if (departmentRepository
                .existsByCodeIgnoreCaseAndIdNot(code, id)) {

            throw new DuplicateDepartmentException(
                    "Department code already exists: " + code
            );
        }

        if (departmentRepository
                .existsByNameIgnoreCaseAndIdNot(name, id)) {

            throw new DuplicateDepartmentException(
                    "Department name already exists: " + name
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