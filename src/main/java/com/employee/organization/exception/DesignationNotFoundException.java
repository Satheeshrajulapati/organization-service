package com.employee.organization.exception;

public class DesignationNotFoundException extends RuntimeException {

    public DesignationNotFoundException(Long id) {
        super("Designation not found with id: " + id);
    }
}