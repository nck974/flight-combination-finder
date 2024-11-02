package com.nichoko.exception.handler;

import com.nichoko.domain.dto.ErrorDTO;
import com.nichoko.exception.AirportNotFoundException;
import com.nichoko.exception.DefaultException;
import com.nichoko.exception.ErrorFetchingDataException;
import com.nichoko.exception.InvalidDateException;
import com.nichoko.exception.MissingParametersExceptions;
import com.nichoko.exception.NoConnectionsFoundException;
import com.nichoko.exception.NoFlightsFoundException;
import com.nichoko.exception.NoResultsForSearchException;
import com.nichoko.exception.TooManyConnectionsException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

@Provider
@JBossLog
public class DefaultExceptionMapper implements ExceptionMapper<DefaultException> {

    @Override
    public Response toResponse(DefaultException exception) {

        ErrorDTO error = new ErrorDTO(exception.getCode(), exception.getMessage());

        Status status = Status.NOT_IMPLEMENTED;

        if (exception instanceof ErrorFetchingDataException) {

            Throwable originalException = ((ErrorFetchingDataException) exception).getOriginalException();
            log.error(originalException.getMessage());
            originalException.printStackTrace();

            status = Status.INTERNAL_SERVER_ERROR;
        } else if (exception instanceof InvalidDateException || exception instanceof TooManyConnectionsException
                || exception instanceof MissingParametersExceptions) {
            status = Status.BAD_REQUEST;
        } else if (exception instanceof NoConnectionsFoundException || exception instanceof NoFlightsFoundException
                || exception instanceof AirportNotFoundException || exception instanceof NoResultsForSearchException) {
            status = Status.NOT_FOUND;
        }

        return Response.ok(error).status(status).build();
    }
}