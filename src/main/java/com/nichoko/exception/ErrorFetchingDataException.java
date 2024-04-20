package com.nichoko.exception;

import lombok.Getter;

@Getter
public class ErrorFetchingDataException extends DefaultException {
    final Throwable originalException;

    public ErrorFetchingDataException(Throwable e) {
        super(5001, "No flights found");
        this.originalException = e;
    }
}
