package com.sporty.ticketing.exception;

/**
 * Domain-level 404.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) { super(msg); }
}
