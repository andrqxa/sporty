package com.sporty.ticketing.exception;

/**
 * Exception indicating a conflict in the current state of the resource.
 * <p>
 * Typically mapped to an HTTP 409 (Conflict) response in the REST API layer.
 * This is thrown when an operation cannot be completed because it would
 * result in a conflict with the current state of the system, such as
 * attempting to update or acquire a resource that is locked or already in use.
 */
public class ConflictException extends RuntimeException {

    /**
     * Creates a new {@code ConflictException} with the specified detail message.
     *
     * @param msg a description of the conflict
     */
    public ConflictException(String msg) {
        super(msg);
    }
}
