package com.nichoko.exception;


import com.nichoko.domain.dto.ErrorDTO;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ErrorFetchingDataExceptionMapper implements ExceptionMapper<ErrorFetchingDataException> {

    @Override
    public Response toResponse(ErrorFetchingDataException exception) {
        
        ErrorDTO error = new ErrorDTO(exception.getCode(), exception.getMessage());

        Status status = Status.INTERNAL_SERVER_ERROR;
        if (exception.getCode() == 400){
            return Response.ok(error).status(Status.BAD_REQUEST).build();
        }
        
        if (exception.getCode() == 404){
            return Response.ok(error).status(Status.NOT_FOUND).build();
        }
        
        return Response.ok(error).status(status).build();
    }
}