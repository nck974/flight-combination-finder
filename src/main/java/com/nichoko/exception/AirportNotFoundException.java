package com.nichoko.exception;

public class AirportNotFoundException extends DefaultException {

    public AirportNotFoundException(String iataCode) {
        super(4043, "The airport with code " + iataCode + " could not be found.");
    }
}
