package com.sporty.ticketing.exception;

/**
 * Exception indicating that a requested resource could not be found.
 * <p>
 * Typically mapped to an HTTP 404 (Not Found) response in the REST API layer.
 * This is thrown when the specified resource identifier does not correspond
 * to any existing entity in the system.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Creates a new {@code NotFoundException} with the specified detail message.
     *
     * @param msg a description of the missing resource
     */
    public NotFoundException(String msg) {
        super(msg);
    }
}
