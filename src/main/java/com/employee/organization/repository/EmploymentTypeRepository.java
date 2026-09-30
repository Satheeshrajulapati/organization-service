package com.employee.organization.repository;

import com.employee.organization.entity.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmploymentTypeRepository
        extends JpaRepository<EmploymentType, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            Long id
    );

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            Long id
    );

    List<EmploymentType> findAllByActiveTrueOrderByNameAsc();
}