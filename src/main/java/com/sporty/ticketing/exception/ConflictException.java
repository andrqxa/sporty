package com.sporty.ticketing.exception;

/**
 * Domain-level 409.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String msg) { super(msg); }
}
