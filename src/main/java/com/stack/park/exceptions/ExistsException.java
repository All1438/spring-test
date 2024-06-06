package com.stack.park.exceptions;

public class ExistsException extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public ExistsException(String message) {
        super(message);
    }
}
