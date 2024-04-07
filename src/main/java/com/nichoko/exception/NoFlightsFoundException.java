package com.nichoko.exception;

public class NoFlightsFoundException extends DefaultException {

    public NoFlightsFoundException() {
        super(4042, "No flights could be found.");
    }
}
