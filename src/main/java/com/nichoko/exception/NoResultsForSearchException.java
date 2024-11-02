package com.nichoko.exception;

public class NoResultsForSearchException extends DefaultException {

    public NoResultsForSearchException() {
        super(4045, "No results found for the given request.");
    }
}
