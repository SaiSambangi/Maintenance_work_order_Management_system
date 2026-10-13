package com.maintenance.workorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a duplicate entry is attempted.
 * Example: Creating a work order with an already-existing work order number.
 *
 * Returns HTTP 409 Conflict to the client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
