package com.nichoko.exception;

import lombok.Getter;

@Getter
public class DefaultException extends RuntimeException {
    private final int code;

    public DefaultException(int code, String message) {
        super(message);
        this.code = code;
    }
}
