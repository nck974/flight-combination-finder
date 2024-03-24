package com.nichoko.exception;

import com.nichoko.domain.dto.ErrorDTO;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NoConnectionsFoundExceptionMapper implements ExceptionMapper<NoConnectionsFoundException> {

    @Override
    public Response toResponse(NoConnectionsFoundException exception) {

        ErrorDTO error = new ErrorDTO(exception.getCode(), exception.getMessage());

        return Response.ok(error).status(Status.NOT_FOUND).build();
    }
}