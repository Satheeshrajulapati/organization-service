package com.employee.organization.exception;

public class EmploymentTypeNotFoundException extends RuntimeException {

    public EmploymentTypeNotFoundException(Long id) {
        super("Employment type not found with id: " + id);
    }
}