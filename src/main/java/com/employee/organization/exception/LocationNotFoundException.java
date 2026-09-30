package com.employee.organization.exception;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException(Long id) {
        super("Location not found with id: " + id);
    }
}