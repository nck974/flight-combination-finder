package com.nichoko.exception;

public class TooManyConnectionsException extends DefaultException {

    public TooManyConnectionsException() {
        super(40002, "It is not possible to search more than 3 connections");
    }
}
