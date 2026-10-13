package com.maintenance.workorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a business rule is violated.
 * Examples:
 *   - Work order number already exists
 *   - CRITICAL work order has no assigned technician
 *   - Invalid status transition (e.g. COMPLETED → OPEN)
 *   - Scheduled date is in the past
 *   - OUT_OF_SERVICE asset receiving a non-emergency work order
 *
 * Returns HTTP 400 Bad Request to the client.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
