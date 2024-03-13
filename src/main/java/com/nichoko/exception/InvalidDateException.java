package com.nichoko.exception;

import lombok.Getter;

@Getter()
public class InvalidDateException extends RuntimeException {
    private final int code;
    private final String message;

    public InvalidDateException(String message, int code) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
