package com.employee.organization.exception;

public class DuplicateEmploymentTypeException extends RuntimeException {

    public DuplicateEmploymentTypeException(String message) {
        super(message);
    }
}