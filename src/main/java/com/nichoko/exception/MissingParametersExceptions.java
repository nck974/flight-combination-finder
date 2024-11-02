package com.nichoko.exception;

public class MissingParametersExceptions extends DefaultException {
    public MissingParametersExceptions(String parameters) {
        super(4046, "The following parameter are missing: " + parameters);
    }
}
