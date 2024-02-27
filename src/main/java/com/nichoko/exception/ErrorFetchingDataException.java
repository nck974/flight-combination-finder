package com.nichoko.exception;

import lombok.Getter;

@Getter()
public class ErrorFetchingDataException extends RuntimeException {
    private final int code;
    private final String message;

    public ErrorFetchingDataException(String message, int code) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
