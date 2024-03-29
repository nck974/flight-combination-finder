package com.nichoko.exception;

import com.nichoko.domain.dto.ErrorDTO;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<DefaultException> {

    @Override
    public Response toResponse(DefaultException exception) {

        ErrorDTO error = new ErrorDTO(exception.getCode(), exception.getMessage());

        Status status = Status.NOT_IMPLEMENTED;

        if (exception instanceof ErrorFetchingDataException) {
            status = Status.INTERNAL_SERVER_ERROR;
        } else if (exception instanceof InvalidDateException || exception instanceof TooManyConnectionsException) {
            status = Status.BAD_REQUEST;
        } else if (exception instanceof NoConnectionsFoundException || exception instanceof NoFlightsFoundException) {
            status = Status.NOT_FOUND;
        }

        return Response.ok(error).status(status).build();
    }
}