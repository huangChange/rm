package com.shell.common.exception;

public class ApiInputException extends RuntimeException {

    private static final Long serialVersionUID = 1L;

    public ApiInputException(String message) {
        super(message);
    }

    public ApiInputException(String message, Throwable cause) {
        super(message, cause);
    }

}
