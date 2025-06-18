package com.alienworkspace.cdr.patient.exception;

import static java.lang.String.format;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a resource is not found.
 *
 * @author Ikenumah (enumahinm@gmail.com)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with a detailed message.
     *
     * @param entity the name of the entity that was not found
     * @param field the field used to search for the entity
     * @param value the value of the field used to search for the entity
     */
    public ResourceNotFoundException(String entity, String field, String value) {
        super(format("%s with %s of '%s' not found", entity, field, value));
    }
}
