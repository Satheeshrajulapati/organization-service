package com.employee.organization.repository;

import com.employee.organization.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository
        extends JpaRepository<Location, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            Long id
    );

    List<Location> findAllByActiveTrueOrderByNameAsc();

    List<Location> findAllByCityIgnoreCaseOrderByNameAsc(
            String city
    );

    List<Location> findAllByCountryIgnoreCaseOrderByNameAsc(
            String country
    );
}