package com.nichoko.exception;

public class ErrorFetchingDataException extends DefaultException {

    public ErrorFetchingDataException() {
        super(5001, "No flights found");
    }
}
