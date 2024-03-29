package com.nichoko.exception;

public class InvalidDateException extends DefaultException {

    public InvalidDateException() {
        super(4001, "Invalid date provided range");
    }
}
