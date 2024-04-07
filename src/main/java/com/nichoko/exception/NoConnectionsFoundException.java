package com.nichoko.exception;

public class NoConnectionsFoundException extends DefaultException {

    public NoConnectionsFoundException() {
        super(4041, "No connections could be found.");
    }
}
