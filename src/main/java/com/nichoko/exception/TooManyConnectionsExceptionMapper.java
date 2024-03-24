package com.nichoko.exception;

import com.nichoko.domain.dto.ErrorDTO;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TooManyConnectionsExceptionMapper implements ExceptionMapper<TooManyConnectionsException> {

    @Override
    public Response toResponse(TooManyConnectionsException exception) {

        ErrorDTO error = new ErrorDTO(exception.getCode(), exception.getMessage());

        return Response.ok(error).status(Status.BAD_REQUEST).build();
    }
}